/**
 * 爬虫类
 */
package crawler;


import entity.Video;
import mysql.DBHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class VideoCrawler {
    private static String crawlerUrl = "http://api.bilibili.com/archive_rank/getarchiverankbypartion";//?tid=xx&pn=xx
    //根据请求url得到response
    private static HttpResponse getResponse(String url)
    {
        HttpClient client = HttpClients.createDefault();
        HttpGet req = new HttpGet(url);
        req.addHeader("Accept","application/json,text/javascript,*/*;q=0.01");
        req.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        req.addHeader("AcceptLanguage", "zh-CN,zh;q=0.8");
        req.addHeader("Content-Type", "text/html;charset=UTF-8");
        req.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
        HttpResponse response = null;
        try {
            response =  client.execute(req);
        } catch (IOException e) {
            System.err.println("connect to "+url+" error,please try again later.");
            e.printStackTrace();
        }
        return response;
    }
    //得到总页数
    private static int getPageNum(String tid)
    {
        HttpResponse response = getResponse(crawlerUrl+"?tid="+tid);
        HttpEntity respEntity = response.getEntity();
        int pageNum = 0;
        try {
            String content = EntityUtils.toString(respEntity);
            JSONObject res = new JSONObject(content);
            JSONObject page = res.getJSONObject("data").getJSONObject("page");
            pageNum = (page.getInt("count")-1)/page.getInt("size")+1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageNum;
    }
    //爬取某个分区的所有视频
    public static void getVideo(String tid)
    {
        int pageNum = getPageNum(tid);
        for(int i=1;i<=pageNum;i++)
        {
            try {
                HttpResponse response = getResponse(crawlerUrl+"?tid="+tid+"&pn="+i);
                HttpEntity entity = response.getEntity();
                JSONObject res = new JSONObject(EntityUtils.toString(entity));
                JSONArray videos = res.getJSONObject("data").getJSONArray("archives");
                for(int j=0;j<videos.length();j++)
                {
                    JSONObject vObj = videos.getJSONObject(j);
                    Video v = new Video();
                    v.setId(String.valueOf(vObj.getInt("aid")));
                    v.setTitle(vObj.getString("title"));
                    v.setAuthor(vObj.getString("author"));
                    v.setCategory(vObj.getString("tname"));
                    v.setCoin(vObj.getJSONObject("stat").getInt("coin"));
                    v.setFavorite(vObj.getInt("favorites"));
                    if(vObj.get("play").equals("--"))
                        v.setPlay(0);
                    else
                        v.setPlay(vObj.getInt("play"));
                    v.setBarrage(vObj.getInt("video_review"));
                    v.setComment(vObj.getJSONObject("stat").getInt("reply"));
                    v.setFluency(calFluency(v));
                    if(DBHelper.insertVideo(v)){
                        System.out.println("insert video "+v.getTitle()+" successfully");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("爬取第"+i+"页出错,已跳过该错误");
                continue;
            }
        }
    }
    //calculate fluency
    private static double calFluency(Video v)
    {
        double fluency = 0;
        fluency+=v.getCoin()/500.0;
        fluency+=v.getFavorite()/250.0;
        fluency+=v.getPlay()/10000.0;
        fluency+=v.getBarrage()/100.0;
        fluency+=v.getComment()/100.0;
        return fluency;
    }
    //测试
    public static void main(String[] args) {
        getVideo("22");
    }
}
