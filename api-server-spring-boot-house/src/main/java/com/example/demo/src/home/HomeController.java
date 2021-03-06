package com.example.demo.src.home;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.home.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/homes")
public class HomeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HomeProvider homeProvider;
    @Autowired
    private final HomeService homeService;
    @Autowired
    private final JwtService jwtService;


    public HomeController(HomeProvider homeProvider, HomeService homeService, JwtService jwtService) {
        this.homeProvider = homeProvider;
        this.homeService = homeService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetHomeMainRes> getHomeMain() throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetHomeMainRes getHomeMainRes = homeProvider.getHomeMain(userIdx);
                return new BaseResponse<>(getHomeMainRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //Query String
    @ResponseBody
    @GetMapping("/house-warm") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetHousewarmingRes>> getHw() {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }
            else{
                int userIdx=jwtService.getUserIdx();
                List<GetHousewarmingRes> getHousewarmingRes = homeProvider.getHw(userIdx);
                return new BaseResponse<>(getHousewarmingRes);
            }
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //Query String
    @ResponseBody
    @GetMapping("/picture") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetPictureRes>> getPicture() {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }
            else{
                int userIdx=jwtService.getUserIdx();
                List<GetPictureRes> getPictureRes = homeProvider.getPicture(userIdx);
                return new BaseResponse<>(getPictureRes);
            }
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @GetMapping("/picture/{picturepostIdx}/comments")
    public BaseResponse<List<GetPictureReviewRes>> getReviews(@PathVariable("picturepostIdx") int picturepostIdx) {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }
            else{
                int userIdx=jwtService.getUserIdx();
                List<GetPictureReviewRes> getPictureReviewRes = homeProvider.getReviews(userIdx,picturepostIdx);
                return new BaseResponse<>(getPictureReviewRes);
            }
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/picture/{picturepostIdx}")
    public BaseResponse<GetPicturePostRes> getPicturePost(@PathVariable("picturepostIdx")int picturepostIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                GetPicturePostRes getPicturePostRes = homeProvider.getPicturePost(userIdx,picturepostIdx);
                return new BaseResponse<>(getPicturePostRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/picture/{picturepostIdx}/comment")
    public BaseResponse<PostPictureReviewRes> createPictureReview(@RequestBody PostPictureReviewReq postPictureReviewReq,@PathVariable("picturepostIdx")int picturepostIdx ) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PostPictureReviewRes postPictureReviewRes = homeService.createPictureReview(postPictureReviewReq,picturepostIdx,userIdx);  // ????????? ?????? ????????? service?????? ??????????????? UserService??? ?????? userService?????? ?????????, ????????? ????????? ????????? createUser??? ??????
                return new BaseResponse<>(postPictureReviewRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    @ResponseBody
    @PatchMapping("/picture/{picturepostIdx}/status")
    public BaseResponse<PatchPictureRes> patchPicture(@PathVariable("picturepostIdx")int picturepostIdx ) throws BaseException {  // json?????? ??????????????? ????????? ????????? ?????? ????????? -> PostUserReq??? ?????? ????????? ?????? ?????? ????????? ???????????? ??????
        try {
            if (jwtService.getJwt() == null) {
                return new BaseResponse<>(EMPTY_JWT);
            } else {
                int userIdx = jwtService.getUserIdx();
                PatchPictureRes patchPictureRes = homeService.patchPicturePostStatus(picturepostIdx, userIdx);  // ????????? ?????? ????????? service?????? ??????????????? UserService??? ?????? userService?????? ?????????, ????????? ????????? ????????? createUser??? ??????
                return new BaseResponse<>(patchPictureRes);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PatchMapping("/house-warm/{hwIdx}/status")
    public BaseResponse<PatchHWRes> patchHW(@PathVariable("hwIdx")int hwIdx ) throws BaseException {  // json?????? ??????????????? ????????? ????????? ?????? ????????? -> PostUserReq??? ?????? ????????? ?????? ?????? ????????? ???????????? ??????
        try {
            if (jwtService.getJwt() == null) {
                return new BaseResponse<>(EMPTY_JWT);
            } else {
                int userIdx = jwtService.getUserIdx();
                PatchHWRes patchHWRes = homeService.patchHWStatus(hwIdx, userIdx);  // ????????? ?????? ????????? service?????? ??????????????? UserService??? ?????? userService?????? ?????????, ????????? ????????? ????????? createUser??? ??????
                return new BaseResponse<>(patchHWRes);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/picture/comment/{commentIdx}/status")
    public BaseResponse<PatchCommentRes> patchComment(@PathVariable("commentIdx")int hwIdx ) throws BaseException {  // json?????? ??????????????? ????????? ????????? ?????? ????????? -> PostUserReq??? ?????? ????????? ?????? ?????? ????????? ???????????? ??????
        try {
            if (jwtService.getJwt() == null) {
                return new BaseResponse<>(EMPTY_JWT);
            } else {
                int userIdx = jwtService.getUserIdx();
                PatchCommentRes patchCommentRes = homeService.patchComment(hwIdx, userIdx);  // ????????? ?????? ????????? service?????? ??????????????? UserService??? ?????? userService?????? ?????????, ????????? ????????? ????????? createUser??? ??????
                return new BaseResponse<>(patchCommentRes);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping ("/heart/{evalableIdx}/{contentIdx}")
    public BaseResponse<PatchHeartRes> patchHeartRes(@PathVariable("evalableIdx") int evalableIdx,@PathVariable("contentIdx") int contentIdx) throws BaseException {
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }

            else{
                int userIdx=jwtService.getUserIdx();
                PatchHeartRes patchHeartRes = homeService.patchHeart(userIdx,evalableIdx,contentIdx);
                return new BaseResponse<>(patchHeartRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}