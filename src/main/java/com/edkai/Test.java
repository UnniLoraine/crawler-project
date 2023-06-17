package com.edkai;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.edkai.modle.User;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * hellenhuang0710@gmail.com       Yj19881217@123
 */
public class Test {


    public static void main(String[] args) throws IOException {
        Test test = new Test();
        long star = System.currentTimeMillis();
        DecimalFormat df2 = new DecimalFormat("0.00000");
        //获取所有列表
        Map userAllMap = test.getUrl("https://facade.upfluence.co/api/v1/matches?criterias%5B0%5D%5Bvalue%5D=Embroidery&criterias%5B0%5D%5Bweight%5D=1&criterias%5B0%5D%5Bfield%5D=all&criterias%5B0%5D%5Btype%5D=should&current_list=151997&filters%5B0%5D%5Bslug%5D=instagramFollowers&filters%5B0%5D%5Bname%5D=instagram.followers&filters%5B0%5D%5Btype%5D=range-int&filters%5B0%5D%5Bvalue%5D%5Bfrom%5D=&filters%5B0%5D%5Bvalue%5D%5Bto%5D=&filters%5B1%5D%5Bslug%5D=tiktokFans&filters%5B1%5D%5Bname%5D=tiktok.followers&filters%5B1%5D%5Btype%5D=range-int&filters%5B1%5D%5Bvalue%5D%5Bfrom%5D=&filters%5B1%5D%5Bvalue%5D%5Bto%5D=&filters%5B2%5D%5Bslug%5D=youtubeFollowers&filters%5B2%5D%5Bname%5D=youtube.followers&filters%5B2%5D%5Btype%5D=range-int&filters%5B2%5D%5Bvalue%5D%5Bfrom%5D=&filters%5B2%5D%5Bvalue%5D%5Bto%5D=&filters%5B3%5D%5Bslug%5D=pinterestFollowers&filters%5B3%5D%5Bname%5D=pinterest.followers&filters%5B3%5D%5Btype%5D=range-int&filters%5B3%5D%5Bvalue%5D%5Bfrom%5D=&filters%5B3%5D%5Bvalue%5D%5Bto%5D=&page=1&per_page=100&should_save=true");
        // 得到所有用户id
        List<User> userList = new ArrayList<>();
        JSONArray influencers = (JSONArray) userAllMap.get("influencers");
        for (Object influencer : influencers) {
            User user = new User();
            JSONObject jsonObject = (JSONObject) influencer;
            Object id = jsonObject.get("id");
            // 1.揭示邮箱
            test.revealMail(id);
            // 2.获取每个用户的详细信息
            Map userMap = test.getUrl("https://facade.upfluence.co/api/v1/influencers/" + id);
            // =====================开始获取详细信息======================
            // 3. 获取用户influencer平台信息
            JSONObject userInfluencer = (JSONObject) userMap.get("influencer");
            //email
            String email = userInfluencer.get("email").toString();
            user.setEmail(email);
            String location = userInfluencer.get("country").toString();
            user.setLocation(location);
            // 4.获取用户ins平台信息
            JSONArray userInstagrams = (JSONArray) userMap.get("instagrams");
            if (CollUtil.isNotEmpty(userInstagrams)) {
                JSONObject userInsInfo = (JSONObject) userInstagrams.get(0);
                // 获取brandMentions
                Object insId = userInsInfo.get("id");
                Map brandMentions = test.getUrl("https://facade.upfluence.co/api/v1/media/mentions?media_type=instagram&media_id=" + insId);
                JSONArray mentions = (JSONArray) brandMentions.get("mentions");
                if (CollUtil.isNotEmpty(mentions)) {
                    StringBuilder sb = new StringBuilder();
                    for (Object mention : mentions) {
                        JSONObject met = (JSONObject) mention;
                        sb.append(met.get("id")).append("\n");
                    }
                    user.setBrandMentions(sb.toString());
                }
                // 获取 audience信息

                if (userInsInfo.containsKey("audience")) {
                    JSONObject insAudience = (JSONObject) userInsInfo.get("audience");
                    // 获取年龄分布
                    if (insAudience.containsKey("age_image_extractor")) {
                        JSONObject insAge = (JSONObject) insAudience.get("age_image_extractor");
                        JSONObject insAgeValue = (JSONObject) insAge.get("values");
                        StringBuilder sb = new StringBuilder();
                        //先排序，赋值
                        LinkedHashMap insAgeSort = test.sortJsonOb(insAgeValue);
                        insAgeSort.forEach((key, value) -> {
                            sb.append(key).append(":").append(String.format("%.4f", value)).append("\n");
                        });
                        user.setInsAges(sb.toString());
                    }
                    // 获取城市分布
                    if (insAudience.containsKey("city_last_picture")) {
                        JSONObject insCity = (JSONObject) insAudience.get("city_last_picture");
                        JSONObject insCityValue = (JSONObject) insCity.get("values");
                        if (insCityValue != null) {
                            // 先排序
                            LinkedHashMap citySortMap = test.sortJsonOb(insCityValue);
                            StringBuilder sb = new StringBuilder();
                            Set keySet = citySortMap.keySet();
                            int i = 0;
                            for (Object key : keySet) {
                                i++;
                                sb.append(key).append(":").append(String.format("%.4f", citySortMap.get(key))).append("\n");
                                if (i >= 5) {
                                    break;
                                }
                            }
                            user.setInsCities(sb.toString());
                        }

                    }
                    // 获取国家分布
                    if (insAudience.containsKey("country_last_picture")) {
                        JSONObject insCountry = (JSONObject) insAudience.get("country_last_picture");
                        JSONObject insCouValue = (JSONObject) insCountry.get("values");
                        if (insCouValue != null) {
                            // 先排序
                            LinkedHashMap insCouSortMap = test.sortJsonOb(insCouValue);
                            StringBuilder sb = new StringBuilder();
                            Set keySet = insCouSortMap.keySet();
                            int i = 0;
                            for (Object key : keySet) {
                                i++;
                                sb.append(key).append(":").append(String.format("%.4f", insCouSortMap.get(key))).append("\n");
                                if (i >= 5) {
                                    break;
                                }
                            }
                            user.setInsCountries(sb.toString());
                        }

                    }
                    // 性别分布
                    if (insAudience.containsKey("gender_image_extractor")) {
                        JSONObject insGender = (JSONObject) insAudience.get("gender_image_extractor");
                        JSONObject insGenderValue = (JSONObject) insGender.get("values");
                        if (insGenderValue != null) {
                            // 先排序
                            StringBuilder sb = new StringBuilder();
                            for (Map.Entry<String, Object> entry : insGenderValue) {
                                sb.append(entry.getKey()).append(":").append(String.format("%.4f", entry.getValue())).append("\n");
                            }
                            user.setInsGender(sb.toString());
                        }
                    }
                }
                // 获取ins点赞
                Object insSubscribe = userInsInfo.get("followers");
                user.setInsSubscribers(insSubscribe.toString());
                // 获取 avg_engagement_rate
                Object insEngageRate = userInsInfo.get("engagement_rate");
                if (!insEngageRate.toString().equals("null")) {
                    user.setInsAvgEngageRate(String.format("%.4f", insEngageRate));
                }

                // 获取 average_likes
                Object insAvgLike = userInsInfo.get("average_likes");
                if (!insAvgLike.toString().equals("null")) {
                    user.setInsAvgLikes(String.format("%.4f", insAvgLike));
                }

                // 获取 avg_views
//                userInsInfo.get("");
                // 获取 username
                Object insUserName = userInsInfo.get("username");
                user.setInsName(insUserName.toString());
                // 获取suggestPrice
                Object suggestPrice = userInsInfo.get("recommended_price");
                if (!suggestPrice.toString().equals("null")) {
                    Integer price = Integer.valueOf(suggestPrice.toString());
                    user.setInsSugPrice(String.valueOf((price / 100)));
                }
            }
            // 5.获取用户tiktok信息
            JSONArray userTiktoks = (JSONArray) userMap.get("tiktoks");
            if (CollUtil.isNotEmpty(userTiktoks)) {
                JSONObject userTikTok = (JSONObject) userTiktoks.get(0);
                Object tiktokId = userTikTok.get("id");
                // 获取 audience信息
                Map tiktokAud = test.getUrl("https://facade.upfluence.co/api/v1/audience?media_type=tiktok&media_id=" + tiktokId);
                if (CollUtil.isNotEmpty(tiktokAud)) {
                    // 性别
                    JSONObject audienceGender = (JSONObject) tiktokAud.get("audience_gender");
                    if (audienceGender != null) {
                        JSONObject gender = (JSONObject) audienceGender.get("values");
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, Object> entry : gender) {

                            sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
                        }
                        user.setTikGender(sb.toString());
                    }

                    // 年龄
                    JSONObject audienceAge = (JSONObject) tiktokAud.get("audience_age");
                    if (audienceAge != null) {
                        JSONObject age = (JSONObject) audienceAge.get("values");
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, Object> entry : age) {
                            sb.append(entry.getKey()).append(":").append((entry.getValue())).append("\n");
                        }
                        user.setTikAges(sb.toString());
                    }
                    // 国家
                    JSONObject audienceCountry = (JSONObject) tiktokAud.get("audience_country");
                    if (audienceCountry != null) {
                        JSONObject country = (JSONObject) audienceCountry.get("values");
                        StringBuilder sb = new StringBuilder();
                        LinkedHashMap linkedHashMap = test.sortJsonOb(country);
                        Set keySet = linkedHashMap.keySet();
                        int i = 0;
                        for (Object key : keySet) {
                            i++;
                            sb.append(key).append(":").append(linkedHashMap.get(key)).append("\n");
                            if (i >= 5) {
                                break;
                            }
                        }
                        user.setTikCountries(sb.toString());
                    }
                }
                // 获取tiktok点赞
                Object insSubscribe = userTikTok.get("followers");
                user.setTikSubscribers(insSubscribe.toString());
                // 获取 avg_engagement_rate
                Object insEngageRate = userTikTok.get("engagement_rate");
                if (!insEngageRate.toString().equals("null")) {
                    user.setTikAvgEngageRate(String.format("%.4f", insEngageRate));
                }

                // 获取 average_likes
                Object insAvgLike = userTikTok.get("average_likes");
                if (!insAvgLike.toString().equals("null")) {
                    user.setTikAvgLikes(String.format("%.4f", insAvgLike));
                }

                // 获取 username
                Object insUserName = userTikTok.get("name");
                user.setTikName(insUserName.toString());
                // 获取 ave_views
                Object avgViews = userTikTok.get("average_plays");
                if (!avgViews.toString().equals("null")) {
                    user.setTikAvgViews(String.format("%.4f", avgViews));
                }
                // 获取建议价格
//                user.setTikSugPrice();
            }
            // 6.获取用户pins信息
            JSONArray userPinterests = (JSONArray) userMap.get("pinterests");
            if (CollUtil.isNotEmpty(userPinterests)) {
                JSONObject userPin = (JSONObject) userPinterests.get(0);
                // 获取ins点赞
                Object pinSubscribe = userPin.get("followers");
                user.setPinSubscribers(pinSubscribe.toString());
                // 获取 avg_engagement_rate
                Object pinEngageRate = userPin.get("engagement_rate");
                if (!pinEngageRate.toString().equals("null")) {
                    user.setPinAvgEngageRate(String.format("%.4f", pinEngageRate));
                }
                // 获取 average_likes
                Object pinAvgLike = userPin.get("average_likes");
                if (!pinAvgLike.toString().equals("null")) {
                    user.setPinAvgLikes(String.format("%.4f", pinAvgLike));
                }
                // 获取 username
                Object pinUserName = userPin.get("username");
                user.setPinName(pinUserName.toString());
                // 获取 ave_views
//                Object avgViews = userPin.get("average_plays");
//                tikTok.setAvgViews(avgViews.toString());
                // 获取建议价格

            }
            // 5.获取用户youtube信息
            JSONArray userYoutubes = (JSONArray) userMap.get("youtubes");
            if (CollUtil.isNotEmpty(userYoutubes)) {
                JSONObject userYou = (JSONObject) userYoutubes.get(0);
                // 获取ins点赞
                Object youSubscribe = userYou.get("followers");
                user.setYouSubscribers(youSubscribe.toString());
                // 获取 avg_engagement_rate
                Object youEngageRate = userYou.get("engagement_rate");
                if (!youEngageRate.toString().equals("null")) {
                    user.setYouAvgEngageRate(String.format("%.4f", youEngageRate));
                }
                // 获取 average_likes
                Object youAvgLike = userYou.get("average_likes");
                if (!youAvgLike.toString().equals("null")) {
                    user.setYouAvgLikes(String.format("%.4f", youAvgLike));
                }
                // 获取 username
                Object youName = userYou.get("name");
                user.setYouName(youName.toString());
                // 获取 ave_views
                Object avgViews = userYou.get("average_views");
                if (!avgViews.toString().equals("null")) {
                    user.setYouAvgViews(String.format("%.4f", avgViews));
                }
                //获取建议价格
                Object sugPrice = userYou.get("recommended_price");
                if (!sugPrice.toString().equals("null")) {
                    Integer price = Integer.valueOf(sugPrice.toString());
                    user.setYouSugPrice(String.valueOf((price / 100)));
                }
            }
            userList.add(user);
        }

        String fileName = "d://数据信息.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, User.class).

                sheet("模板").

                doWrite(userList);
        System.out.println("完成");

    }


    public static Map<String, String> getHeaderMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Authorization", "Bearer +IqW3PVCerowr/0c6cS10EyoV3Pwh7iuL+yOmpU24tYDHPHsjzRkOPmyZAhuC8EtEgz0UfJSGlu7HyDJxjTzbA==");
//        map.put("If-None-Match", "W/\"4f2d71e4d3c40e063d1675e21724cda6\"");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        return map;
    }

    public Map getUrl(String url) {
        HttpResponse httpResponse = HttpRequest.get(url)
                .addHeaders(getHeaderMap())
                .execute();
        Map map = JSONUtil.toBean(httpResponse.body(), Map.class);
        return map;
    }


    public LinkedHashMap sortJsonOb(JSONObject jsonObject) {
        return CollUtil.sortByEntry(jsonObject, (entry1, entry2) -> {
            Double v1 = Double.valueOf(entry1.getValue().toString());
            Double v2 = Double.valueOf(entry2.getValue().toString());
            return Double.compare(v2, v1);
        });
    }


    public void revealMail(Object id) {
        String url = String.format("https://facade.upfluence.co/api/v1/influencers/%s/reveal_email", id);
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(getHeaderMap())
                .execute();
    }

}
