package com.wlqq.chmatch.elk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wlqq.chmatch.queue.LogBlockQueue;
import com.wlqq.chmatch.vo.ELKUpdateTag;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by wei.zhao on 2017/9/21.
 */
public class ElasticSearchClient {

    private String host = "10.2.1.1";

    private int port = 9301;

    private int threadNum = 3;

    private String queryHost;

    private TransportClient client;

    private ExecutorService executorService;

    private static final String LOG_PRE = "logstash-";

    private static final String RESULT_FLAG = "result.res_result_list";


    public ElasticSearchClient() throws UnknownHostException {
        this.executorService = Executors.newFixedThreadPool(threadNum);
        init();
    }



    public void init() throws UnknownHostException {
         client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
    }


    public SearchHit[] query(String index,String key,String val){
        TermQueryBuilder termQuery = termQuery(key, val);
        SearchResponse response = client.prepareSearch(index).setQuery(termQuery).get();

        if (response.getHits().getHits().length > 0){
            return response.getHits().getHits();
        }
        return null;
    }


    public void update(String index,String type,String id,Map<String,Object> map){
        client.prepareUpdate(index,type,id).setDoc(map).get();
    }


    public QueryResult query(String msg){
        QueryResult queryResult = new QueryResult();
        String[] split = msg.split("_");
        int len = split.length;
        String ts = split[len - 1];
        String originId = split[len -2];
        String userId = split[len -3];
        String updateTag = split[0];
        long tm = Long.parseLong(ts);
        DateTime dateTime = new DateTime(tm);
        String logDate = dateTime.toString("yyyy.MM.dd");
        SearchHit[] query = query(LOG_PRE + logDate, originId, userId);
        queryResult.setUserId(userId);
        queryResult.setOriginId(originId);
        queryResult.setUpdateTag(updateTag.equals("") ? ELKUpdateTag.CLICK : ELKUpdateTag.CALL);
        queryResult.setHits(query);
        return queryResult;
    }

    private void update(QueryResult queryResult) {
        SearchHit[] hits = queryResult.getHits();
        if(hits == null && hits.length ==0) {
            return ;
        }
        for (SearchHit hit : hits){
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (sourceAsMap != null){
                Object o = sourceAsMap.get(RESULT_FLAG);
                JSONArray array = JSON.parseArray(o.toString());
                if(array != null){
                    for (int i = 0 ;i < array.size() ; i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        if(jsonObject != null){
                            if(jsonObject.get("user_id").equals(queryResult.getUserId())){
                                if(queryResult.getUpdateTag().equals(ELKUpdateTag.CLICK)){
                                    Integer click_label = (Integer) jsonObject.get("click_label");
                                    if(click_label == null){
                                        jsonObject.put("click_label",1);
                                    }else{
                                        jsonObject.put("click_label",click_label+1);
                                    }
                                }else if(queryResult.getUpdateTag().equals(ELKUpdateTag.CALL)){
                                    Integer call_label = (Integer) jsonObject.get("call_label");
                                    if(call_label == null){
                                        jsonObject.put("call_label",1);
                                    }else{
                                        jsonObject.put("call_label",call_label+1);
                                    }
                                }

                            }
                        }
                    }
                }
                Map<String,Object> result = new HashMap<>(1);
                result.put(RESULT_FLAG,array);
                update(hit.getIndex(),hit.getType(),hit.getId(),result);
            }
        }
    }


    public void run(){
        for (int i = 0 ; i < threadNum; i++){
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        String msg = (String) LogBlockQueue.poll();
                        if(msg == null){
                            continue;
                        }

                        QueryResult query = query(msg);
                        update(query);
                    }
                }

            });
        }
    }


    /**
     * 开始消费
     */
    public void start(){
        run();
    }


    private class QueryResult{
        private String userId;
        private String originId;
        private ELKUpdateTag updateTag;
        private SearchHit[] hits;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getOriginId() {
            return originId;
        }

        public void setOriginId(String originId) {
            this.originId = originId;
        }

        public SearchHit[] getHits() {
            return hits;
        }

        public void setHits(SearchHit[] hits) {
            this.hits = hits;
        }

        public ELKUpdateTag getUpdateTag() {
            return updateTag;
        }

        public void setUpdateTag(ELKUpdateTag updateTag) {
            this.updateTag = updateTag;
        }
    }


    public static void main(String args[]) throws UnknownHostException {
        ElasticSearchClient client = new ElasticSearchClient();
        client.start();
    }



}
