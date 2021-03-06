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

    public List<GetHousewarmingRes> getHw(int userIdx){
        return this.jdbcTemplate.query("select thumbnailImageUrl,\n" +
                        "       (select count(Scrap.contentIdx)\n" +
                        "\n" +
                        "        FROM Scrap\n" +
                        "\n" +
                        "        WHERE Scrap.contentIdx = Housewarming.idx\n" +
                        "          AND Scrap.evalableIdx = Housewarming.evalableIdx\n" +
                        "          and Scrap.status = 'T')                                                        as scrapCount,\n" +
                        "       Case\n" +
                        "           When ProductScrap.status is Null THEN 'F'\n" +
                        "           ELSE ProductScrap.status END                                                  as scrapStatus,\n" +
                        "       U.idx                                                                             as userIdx,\n" +
                        "       U.Name                                                                            as userName,\n" +
                        "       Housewarming.name                                                                 as title,\n" +
                        "       if(timestampdiff(day, Housewarming.createdAt, current_timestamp()) < 7, 'T', 'F') as newContent,\n" +
                        "       U.userimageUrl\n" +
                        "from Housewarming\n" +
                        "         left outer join (select User.idx, User.name, contentIdx, evalableIdx, Scrap.status\n" +
                        "                          from User\n" +
                        "                                   inner join Scrap\n" +
                        "                                              on Scrap.userIdx = User.idx\n" +
                        "                          where User.idx = ?) ProductScrap\n" +
                        "                         on ProductScrap.contentIdx = Housewarming.idx and\n" +
                        "                            ProductScrap.evalableIdx = Housewarming.evalableIdx\n" +
                        "         inner join User U\n" +
                        "                    on U.idx = Housewarming.userIdx\n" +
                        "where Housewarming.status = 'T'",
                (rs, rowNum) -> new GetHousewarmingRes(
                        rs.getString("thumbnailImageUrl"),
                        rs.getInt("scrapCount"),
                        rs.getString("scrapStatus"),
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("title"),
                        rs.getString("newContent"),
                        rs.getString("userimageUrl")),
                userIdx);
    }

    public List<GetPictureRes> getPicture(int userIdx){
        return this.jdbcTemplate.query("select p.idx                            as picturepostIdx,\n" +
                        "       (select count(Scrap.contentIdx)\n" +
                        "\n" +
                        "        FROM Scrap\n" +
                        "\n" +
                        "        WHERE Scrap.contentIdx = p.idx\n" +
                        "          AND Scrap.evalableIdx = p.evalableIdx\n" +
                        "          and Scrap.status = 'T')       as scrapCount,\n" +
                        "       Case\n" +
                        "           When ProductScrap.status is Null THEN 'F'\n" +
                        "           ELSE ProductScrap.status END as scrapStatus,\n" +
                        "       (select count(Heart.contentIdx)\n" +
                        "\n" +
                        "        FROM Heart\n" +
                        "\n" +
                        "        WHERE Heart.contentIdx = p.idx\n" +
                        "          AND Heart.evalableIdx = p.evalableIdx\n" +
                        "          and Heart.status = 'T')       as heartCount,\n" +
                        "       Case\n" +
                        "           When Cotentheart.status is Null THEN 'F'\n" +
                        "           ELSE Cotentheart.status END  as heartStatus,\n" +
                        "       (select count(PicturesReview.idx) from PicturesReview where PicturesReview.picturepostIdx=p.idx) as reviewCount,\n" +
                        "       p.userIdx,\n" +
                        "       u.userimageUrl,\n" +
                        "       u.name                           as userName,\n" +
                        "       p.comment,\n" +
                        "       ANY_VALUE(pictureUrl)            as pictureUrl\n" +
                        "from PicturePost p\n" +
                        "         left outer join (select User.idx, User.name, contentIdx, evalableIdx, Scrap.status\n" +
                        "                          from User\n" +
                        "                                   inner join Scrap\n" +
                        "                                              on Scrap.userIdx = User.idx\n" +
                        "                          where User.idx = ?) ProductScrap\n" +
                        "                         on ProductScrap.contentIdx = p.idx and\n" +
                        "                            ProductScrap.evalableIdx = p.evalableIdx\n" +
                        "         left outer join (select User.idx, User.name, contentIdx, evalableIdx, Heart.status\n" +
                        "                          from User\n" +
                        "                                   inner join Heart\n" +
                        "                                              on Heart.userIdx = User.idx\n" +
                        "                          where User.idx = ?) Cotentheart\n" +
                        "                         on Cotentheart.contentIdx = p.idx and\n" +
                        "                            Cotentheart.evalableIdx = p.evalableIdx\n" +
                        "         inner join User U\n" +
                        "                    on U.idx = p.userIdx\n" +
                        "         inner join Pictures p2 on p.idx = p2.picturepostIdx\n" +
                        "         inner join User u on u.idx = p.userIdx\n" +
                        "where p.status = 'T'\n" +
                        "group by p.idx",
                (rs, rowNum) -> new GetPictureRes(
                        rs.getInt("picturepostIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("scrapCount"),
                        rs.getString("scrapStatus"),
                        rs.getInt("heartCount"),
                        rs.getString("heartStatus"),
                        rs.getInt("reviewCount"),
                        rs.getString("userimageUrl"),
                        rs.getString("userName"),
                        rs.getString("comment"),
                        rs.getString("pictureUrl")),
                userIdx,userIdx);
    }

    public List<GetPictureReviewRes> getReviews(int userIdx,int picturepostIdx){
        return this.jdbcTemplate.query("select p.idx as commentIdx,p.picturepostIdx, p.userIdx, u.userimageUrl,u.name as userName\n" +
                        "                       ,p.comment,\n" +
                        "                       case\n" +
                        "                           when timestampdiff(second , p.createdAt, current_timestamp()) < 60\n" +
                        "                               then concat(timestampdiff(second , p.createdAt, current_timestamp()), '???')\n" +
                        "                           when timestampdiff(minute , p.createdAt, current_timestamp()) < 60\n" +
                        "                               then concat(timestampdiff(minute , p.createdAt, current_timestamp()), '???')\n" +
                        "                           when timestampdiff(hour, p.createdAt, current_timestamp()) < 24\n" +
                        "                               then concat(timestampdiff(hour, p.createdAt, current_timestamp()), '??????')\n" +
                        "                           when timestampdiff(day, p.createdAt, current_timestamp()) < 7\n" +
                        "                               then concat(timestampdiff(day, p.createdAt, current_timestamp()), '???')\n" +
                        "                           when timestampdiff(week, p.createdAt, current_timestamp()) < 4\n" +
                        "                                then concat(timestampdiff(week, p.createdAt, current_timestamp()), '???')\n" +
                        "                           when timestampdiff(month , p.createdAt, current_timestamp()) < 12\n" +
                        "                                then concat(timestampdiff(month , p.createdAt, current_timestamp()), '???')\n" +
                        "                           else '1???'\n" +
                        "                       end as howmuchTime\n" +
                        "                               from PicturesReview p\n" +
                        "                inner join User u on p.userIdx = u.idx\n" +
                        "                inner join PicturePost PP on p.picturepostIdx = PP.idx where p.picturepostIdx=? AND p.status='T'",
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

    public List<GetPictureListRes> getPictureImg(int picturepostIdx){
        return this.jdbcTemplate.query("select idx,picturepostIdx,pictureUrl from Pictures\n" +
                        "where picturepostIdx=?",
                (rs, rowNum) -> new GetPictureListRes(
                        rs.getInt("idx"),
                        rs.getString("pictureUrl")
                ),
                picturepostIdx);
    }
    public String getComment(int picturepostIdx){
        return this.jdbcTemplate.queryForObject("select comment from PicturePost where idx = ?\n",String.class,picturepostIdx);
    }

    public int createPictureReview(PostPictureReviewReq postPictureReviewReq,int picturepostIdx,int userIdx){
        this.jdbcTemplate.update("insert into PicturesReview (userIdx,picturepostIdx,comment) Values (?,?,?)",  // insert,update,delete ????????? ??? update??? ???????????? ???
                userIdx,picturepostIdx,postPictureReviewReq.getComment()
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }


    public int checkPicturePost(int picturepostIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select * from PicturePost\n" +
                "where idx=?)",int.class,picturepostIdx);
    }

    public int checkHW(int hwIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select * from Housewarming where idx=?\n" +
                "    ) as exist",int.class,hwIdx);
    }
    public int checkComment(int commentIdx){
        return this.jdbcTemplate.queryForObject("select EXISTS(select * from PicturesReview where idx=?\n" +
                "    ) as exist",int.class,commentIdx);
    }
    public int checkHWUser(int userIdx,int hwIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select userIdx from Housewarming where userIdx = ? and idx = ?) as exist",
                int.class ,userIdx,hwIdx);
    }
    public int checkCommentUser(int userIdx,int commentIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select userIdx from PicturesReview where userIdx = ? and idx = ?) as exist",
                int.class ,userIdx,commentIdx);
    }

    public int checkPictureUser(int userIdx,int picturepostIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select * from PicturePost\n" +
                "where userIdx=? and idx=?)",int.class ,userIdx,picturepostIdx);
    }

    public char checkPicturePostStatus(int userIdx,int picturepostIdx){
        return this.jdbcTemplate.queryForObject("select status from PicturePost \n" +
                "where idx =? and userIdx=?", char.class,picturepostIdx,userIdx);
    }
    public char checkHWstatus(int userIdx,int hwIdx){
        return this.jdbcTemplate.queryForObject("select status from Housewarming\n" +
                "                where idx =? and userIdx=?", char.class,hwIdx,userIdx);
    }

    public char checkCommentstatus(int userIdx,int commentIdx){
        return this.jdbcTemplate.queryForObject("select status from PicturesReview\n" +
                "                where idx =? and userIdx=?", char.class,commentIdx,userIdx);
    }

    public PatchPictureRes patchPicturePostStatus(String status,int userIdx,int picturepostIdx){
        this.jdbcTemplate.update("UPDATE PicturePost set status=? \n" +
                        "where userIdx=? and idx=?",
                status,userIdx,picturepostIdx);
        return this.jdbcTemplate.queryForObject("select userIdx,idx as picturepostIdx,status from PicturePost\n" +
                        "where userIdx=? and idx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PatchPictureRes(
                        rs.getInt("userIdx"),
                        rs.getInt("picturepostIdx"),
                        rs.getString("status")),
                userIdx,picturepostIdx);
    }

    public PatchHWRes patchHWStatus(String status,int userIdx,int hwIdx){
        this.jdbcTemplate.update("UPDATE Housewarming set status=? \n" +
                        "where userIdx=? and idx=?",
                status,userIdx,hwIdx);
        return this.jdbcTemplate.queryForObject("select userIdx,idx as hwIdx,status from Housewarming\n" +
                        "where userIdx=? and idx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PatchHWRes(
                        rs.getInt("userIdx"),
                        rs.getInt("hwIdx"),
                        rs.getString("status")),
                userIdx,hwIdx);
    }
    public PatchCommentRes patchCommentStatus(String status,int userIdx,int commentIdx){
        this.jdbcTemplate.update("UPDATE PicturesReview set status=? \n" +
                        "where userIdx=? and idx=?",
                status,userIdx,commentIdx);
        return this.jdbcTemplate.queryForObject("select userIdx,idx as commentIdx,status from PicturesReview\n" +
                        "where userIdx=? and idx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PatchCommentRes(
                        rs.getInt("userIdx"),
                        rs.getInt("commentIdx"),
                        rs.getString("status")),
                userIdx,commentIdx);
    }

    public int checkHeartExist(int userIdx,int evalableIdx,int contentIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select status from Heart\n" +
                "where evalableIdx=? and contentIdx=? and userIdx=? ) as HeartExist", int.class,evalableIdx,contentIdx,userIdx);
    }
    public char checkHeart(int userIdx,int evalableIdx,int contentIdx){
        return this.jdbcTemplate.queryForObject("select status from Heart \n" +
                "where evalableIdx =? and contentIdx=? and userIdx=?", char.class,evalableIdx,contentIdx,userIdx);
    }
    public PatchHeartRes patchHeart(String status,int userIdx,int evalableIdx,int contentIdx){
        this.jdbcTemplate.update("UPDATE Heart set status=? \n" +
                        "where userIdx=? and evalableIdx=? and contentIdx=?",
                status,userIdx,evalableIdx,contentIdx);
        return this.jdbcTemplate.queryForObject("select idx as heartIdx,status,userIdx,evalableIdx,contentIdx from Heart\n" +
                        "where userIdx=? and evalableIdx=? and contentIdx=?",  // queryForObject??? ????????? ????????? ??? ??????
                (rs, rowNum) -> new PatchHeartRes(
                        rs.getInt("heartIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("evalableIdx"),
                        rs.getInt("contentIdx"),
                        rs.getString("status")),
                userIdx,evalableIdx,contentIdx);
    }


    public PatchHeartRes createHeart(String status, int userIdx,int evalableIdx,int contentIdx){
        this.jdbcTemplate.update("insert into Heart (userIdx,evalableIdx,contentIdx,createdAt,status) VALUES (?,?,?,now(),?)",
                userIdx,evalableIdx,contentIdx,status);
        return this.jdbcTemplate.queryForObject("select idx as heartIdx,userIdx,evalableIdx,contentIdx,status from Heart\n" +
                        "where userIdx=? and evalableIdx=? and contentIdx=? ",
                (rs, rowNum) -> new PatchHeartRes(
                        rs.getInt("heartIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("evalableIdx"),
                        rs.getInt("contentIdx"),
                        rs.getString("status")),
                userIdx,evalableIdx,contentIdx);
    }
    public int checkEvalableExist(int evalableIdx){
        return this.jdbcTemplate.queryForObject("select Exists(select * from Evalable where idx=?)",int.class,evalableIdx);
    }
}