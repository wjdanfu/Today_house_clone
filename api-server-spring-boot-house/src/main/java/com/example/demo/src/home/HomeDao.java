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
                        "       if(timestampdiff(day, Housewarming.createdAt, current_timestamp()) < 7, 'T', 'F') as newContent, U.userimageUrl\n" +
                        "from Housewarming inner join User U\n" +
                        "on U.idx=Housewarming.userIdx",
                (rs, rowNum) -> new GetHousewarmingRes(
                        rs.getString("thumbnailImageUrl"),
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("title"),
                        rs.getString("newContent"),
                        rs.getString("userimageUrl")
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

    public List<GetPictureReviewRes> getReviews(int picturepostIdx){
        return this.jdbcTemplate.query("select p.idx as commentIdx,p.picturepostIdx, p.userIdx, u.userimageUrl,u.name as userName\n" +
                        "       ,p.comment,\n" +
                        "       case\n" +
                        "           when timestampdiff(second , p.createdAt, current_timestamp()) < 60\n" +
                        "               then concat(timestampdiff(second , p.createdAt, current_timestamp()), '초')\n" +
                        "           when timestampdiff(minute , p.createdAt, current_timestamp()) < 60\n" +
                        "               then concat(timestampdiff(minute , p.createdAt, current_timestamp()), '분')\n" +
                        "           when timestampdiff(hour, p.createdAt, current_timestamp()) < 24\n" +
                        "               then concat(timestampdiff(hour, p.createdAt, current_timestamp()), '시간')\n" +
                        "           when timestampdiff(day, p.createdAt, current_timestamp()) < 7\n" +
                        "               then concat(timestampdiff(day, p.createdAt, current_timestamp()), '일')\n" +
                        "           when timestampdiff(week, p.createdAt, current_timestamp()) < 4\n" +
                        "                then concat(timestampdiff(week, p.createdAt, current_timestamp()), '주')\n" +
                        "           when timestampdiff(month , p.createdAt, current_timestamp()) < 12\n" +
                        "                then concat(timestampdiff(month , p.createdAt, current_timestamp()), '달')\n" +
                        "           else '1년'\n" +
                        "       end as howmuchTime\n" +
                        "               from PicturesReview p\n" +
                        "inner join User u on p.userIdx = u.idx\n" +
                        "inner join PicturePost PP on p.picturepostIdx = PP.idx where p.picturepostIdx=?",
                (rs, rowNum) -> new GetPictureReviewRes(
                        rs.getInt("commentIdx"),
                        rs.getInt("picturepostIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("comment"),
                        rs.getString("howmuchTime")),
                picturepostIdx);
    }


}