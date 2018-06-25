/**
 * 数据库操作类
 */
package mysql;

import entity.Video;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper
{
    //数据库连接对象
    private static Connection conn = null;
    //返回数据库连接对象
    private static Connection getConnection()
    {
        String url = "jdbc:mysql://localhost:3306/bilibili?serverTimezone=GMT%2B8";
        String username = "root";
        String password = "zz123456";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url,username,password);

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    //查看是否有该对象
    public static boolean isDuplicate(Video v)
    {
        Connection conn = getConnection();
        return false;
    }
    //向数据库写入数据
    public static boolean insertVideo(Video v)
    {
        Connection conn = getConnection();
        String sql = "insert into video (id,title,author,category,coin,favorite,play,barrage,comment,fluency) values (?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement prstmt = conn.prepareStatement(sql);
            prstmt.setString(1,v.getId());
            prstmt.setString(2,v.getTitle());
            prstmt.setString(3,v.getAuthor());
            prstmt.setString(4,v.getCategory());
            prstmt.setInt(5,v.getCoin());
            prstmt.setInt(6,v.getFavorite());
            prstmt.setInt(7,v.getPlay());
            prstmt.setInt(8,v.getBarrage());
            prstmt.setInt(9,v.getComment());
            prstmt.setDouble(10,v.getFluency());
            prstmt.execute();
            conn.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    //从数据库中找到某个类别影响力排名前5的视频
    public static List<Video> getTopFive(String webSite) throws SQLException {
        Connection conn = getConnection();
        //get category
        String category = "鬼畜调教";
        List<Video> res = new ArrayList<>();
        String sql = "select * from video where category = '"+category+"' order by fluency desc limit 5";
        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                Video v = new Video(rs.getString("id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("category"), rs.getInt("coin"), rs.getInt("favorite"),
                        rs.getInt("play"), rs.getInt("barrage"),rs.getInt("comment"));
                v.setFluency(rs.getDouble("fluency"));
                res.add(v);
            }
            conn.close();
        }catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }
    public static void main(String[] args) throws SQLException {
        Connection conn = getConnection();
        Video v = new Video("110","lalala","zzz","发神经类",
                5,999,111,222,333);
        v.setFluency(17.6);
        if(DBHelper.insertVideo(v))
        {
            System.out.println("successfully inserted!");
        }
        else
        {
            System.out.println("fail to insert!");
        }
        List<Video> vl = getTopFive("");
        for(Video vd : vl)
        {
            System.out.println(vd.getId()+" "+vd.getTitle()+" "+vd.getFluency());
        }
    }
}
