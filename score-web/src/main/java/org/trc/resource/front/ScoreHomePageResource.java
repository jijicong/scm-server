//package org.trc.resource.front;
//
//import com.alibaba.fastjson.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.trc.constants.ScoreConstants;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.Response;
//
///**
// * Created by george on 2016/12/21.
// */
//@Path(ScoreConstants.Route.Home.ROOT)
//public class ScoreHomePageResource {
//
//    private Logger logger = LoggerFactory.getLogger(ScoreHomePageResource.class);
//
//    /**
//     * 用户积分信息
//     *
//     */
//    @GET
//    @Path("/user/score")
//    public Response getUserScore() {
//        String userId = null;
//        JSONObject jsonObject = new JSONObject();
//        try {
//            userId = TxJerseyRestContextFactory.getInstance().getUserId();
//            Score score = scoreService.getScoreByUserId(userId);
//            jsonObject.put("score", ObjectUtils.convertVal(score.getScore(), 0));
//            //获取将过期子账户数据
//            this.getScoreOfBeforeEndDate(jsonObject, userId, score);
//            return TxJerseyTools.returnSuccess(jsonObject.toJSONString());
//        } catch (ScoreException e) {
//            if(ScoreException.BALANCE_NOT_ENOUGH.equals(e.getCode())||ScoreException.ENTITY_NOT_EXIST.equals(e.getCode())){
//                jsonObject.put("score",0);
//                return TxJerseyTools.returnSuccess(jsonObject.toJSONString());
//            }
//            return CustomAck.customError(e.getMessage());
//        } catch (ScoreChildException e) {
//            return CustomAck.customError(e.getMessage());
//        } catch (Exception e) {
//            logger.error("=====>userScore exception", e);
//            return TxJerseyTools.returnAbort(CommonConstants.ErrorCode.ERROR_SERVICE_IN_REST);
//        }
//    }
//
//    /**
//     * 用户tcoin信息
//     *
//     */
//    @GET
//    @Path("/user/tcion")
//    @Logined
//    public Response getUserTcoin() {
//        String userId = null;
//        try {
//            userId = TxJerseyRestContextFactory.getInstance().getUserId();
//            TcoinAck tcoinAck = tcoinService.queryTcoinBalance(userId);
//            JSONObject jsonObject = new JSONObject();
//            if(ExternalserviceResultCodeConstants.Tcoin.SUCCESS.equals(tcoinAck.getResultCode())){
//                jsonObject.put("tcoin",tcoinAck.getAvail());
//                return TxJerseyTools.returnSuccess(ResponseAck.renderSuccess(jsonObject));
//            }else if(ExternalserviceResultCodeConstants.Tcoin.TCOIN_BALANCE_NOT_ENOUGH.equals(tcoinAck.getResultCode())){
//                return CustomAck.customError(ResponseAck.renderFailure602());
//            }else{
//                return CustomAck.customError(ResponseAck.renderFailure601());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("根据userId=>[" + userId + "]查询tcoin余额失败!");
//            return CustomAck.customError(ResponseAck.renderFailure601());
//        }
//    }
//
//    /**
//     *  查询最近过期的一笔积分
//     *
//     * @param jsonObject
//     * @param userId
//     * @param score
//     * @throws IsNULLException
//     */
//    private void getScoreOfBeforeEndDate(JSONObject jsonObject, String userId, Score score) {
//        if(score.getScore()>0) {
//            ScoreChild scoreChild = scoreChildService.getFirstScoreChildByUserId(userId);
//            jsonObject.put("count", scoreChild.getScore());
//            jsonObject.put("validDate", scoreChild.getExpirationTime().getTime());
//        }else{
//            jsonObject.put("count", 0);
//        }
//    }
//
//    @GET
//    @Path("/user/converter")
//    @Logined
//    public Response getConverterList(){
//        String userId = null;
//        try {
//            userId = TxJerseyRestContextFactory.getInstance().getUserId();
//            List<ScoreConverter> scoreConverterList = converterService.selectScoreConverter();
//            JSONObject jsonObject = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//            for(ScoreConverter scoreConverter : scoreConverterList){
//                JSONObject json = new JSONObject();
//                json.put("exchangeCurrency",scoreConverter.getExchangeCurrency());
//                json.put("amount",scoreConverter.getAmount());
//                json.put("score",scoreConverter.getScore());
//                json.put("direction",scoreConverter.getDirection());
//                jsonArray.add(json);
//            }
//            jsonObject.put("infos",jsonArray);
//            return TxJerseyTools.returnSuccess(jsonObject.toJSONString());
//        } catch (ScoreConverterException e) {
//            return CustomAck.customError(e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("====>getConverterList", e);
//            return TxJerseyTools.returnAbort(CommonConstants.ErrorCode.ERROR_SERVICE_IN_REST);
//        }
//    }
//
//}
