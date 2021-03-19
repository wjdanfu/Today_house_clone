package com.example.demo.src.home;


import com.example.demo.src.home.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HomeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetHousewarmingRes> getHw(){
        return this.jdbcTemplate.query("select thumbnailImageUrl, U.idx as userIdx,U.Name as userName, Housewarming.name as title,\n" +
                        "       if(timestampdiff(day, Housewarming.createdAt, current_timestamp()) < 7, 'T', 'F') as newContent\n" +
                        "from Housewarming inner join User U\n" +
                        "on U.idx=Housewarming.userIdx",
                (rs, rowNum) -> new GetHousewarmingRes(
                        rs.getString("thumbnailImageUrl"),
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("title"),
                        rs.getString("newContent")
                ));
    }

    public List<GetPictureRes> getPicture(){
        return this.jdbcTemplate.query("select p.idx as picturepostIdx,\n" +
                        "       p.userIdx,\n" +
                        "       u.userimageUrl,\n" +
                        "       u.name as userName,\n" +
                        "       p.comment,\n" +
                        "       GROUP_CONCAT(pictureUrl) as pictureUrl\n" +
                        "from PicturePost p\n" +
                        "         inner join Pictures p2 on p.idx = p2.picturepostIdx\n" +
                        "         inner join User u on u.idx = p.userIdx group by p.idx",
                (rs, rowNum) -> new GetPictureRes(
                        rs.getInt("picturepostIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("comment"),
                        rs.getString("pictureUrl"))
                );
    }


}