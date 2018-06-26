/**
 * 下载视频类
 */
package crawler;

import entity.Video;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static mysql.DBHelper.getTopFive;

public class VideoDownloader {
    //下载视频
    public static boolean downloadVideo(String downloadAddress,String filePath) throws Exception
    {

        Document doc;
        String filename="";
        String downloadUrl="";
        boolean isdownLoad = false;
        try
        {
            doc=Jsoup.connect(downloadAddress).timeout(3000).get();
            Elements links = doc.getElementsByTag("a");//得到所有链接地址
            for (Element link : links)
            {
                String linkHref = link.attr("abs:href");//得到下载页面的绝对地址
                String linkText = link.text();
                boolean mp4 = true;
                if(linkText.toLowerCase().contains("mp4")&&linkText.contains("("))
                {
                    mp4 = false;
                    filename=link.attr("data-download");//保存文件名
                    downloadUrl=AnalyseWebPage(linkHref, linkText);//得到url
                    isdownLoad = downloadWithPath(filePath, filename, downloadUrl);
                }
                else if(linkHref.toLowerCase().contains("download")&&linkHref.toLowerCase().contains("cid")&&mp4==false)
                {
                    filename=linkHref;
                    downloadUrl=AnalyseWebPage(linkHref, linkText);
                    isdownLoad = downloadWithPath(filePath, filename, downloadUrl);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        if(isdownLoad)
        {
            return true;
        }
        return false;
    }

    //解析下载页面
    public static String AnalyseWebPage(String linkHref, String linkText) throws IOException
    {
        System.out.printf("%s %s\n",linkHref,linkText);
        Document content=Jsoup.connect(linkHref).timeout(3000).get();
        Elements downloadLinks = content.getElementsByTag("a");
        for(Element downloadlink:downloadLinks)
        {
            String linkHref1 = downloadlink.attr("abs:href");
            String linkText1 = downloadlink.text();
            if(linkText1.contains("普通下载"))
            {
                //普通下载可能是个javascript脚本
                System.out.printf("%s %s\n",linkHref1,linkText1);
                return linkHref1;
            }
        }
        return null;
    }

    //下载视频步骤
    public static boolean downloadWithPath(String filePath,String filename,String downloadUrl) throws Exception
    {
        File file = new File(filePath);
        if(!file.exists())
        {
            file.mkdir();
        }
        //判断是flv还是MP4格式
        if(!filename.contains("mp4"))
        {
            filename+=".flv";
        }
        File videofile=new File(filename);//File(file+filename.substring(filename.lastIndexOf("/")));
        /*
         * 下载视频步骤
         */
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = conn.getInputStream();
        //获取字节数组
        byte[] getData = readInputStream(inputStream);
        FileOutputStream fos= new FileOutputStream(videofile);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }

        if(videofile.length()>0)
        {
            return true;
        }
        return false;
    }

    //得到字节数组
    public static  byte[] readInputStream(InputStream inputStream) throws IOException
    {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1)
        {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    //用作测试
    public static void main(String[] args)
    {
        try
        {
            List<Video> vl = getTopFive("");
            for(Video vd : vl)
            {
                if(downloadVideo("http://www.bilibilijj.com/video/av"+vd.getId(), "video/"))
                {
                    System.out.println(vd.getTitle()+" 下载完成！");
                }
                else
                {
                    System.out.println(vd.getTitle()+" 下载失败,请检查网络！");
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
