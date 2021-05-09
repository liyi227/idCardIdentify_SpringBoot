package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.User;
import com.example.demo.entity.IDCard;
import com.example.demo.service.ICardOcrService;
import com.sun.scenario.effect.ImageData;
import jdk.nashorn.internal.ir.IfNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author ly
 * @since 2021/5/8
 */
@RestController()
@RequestMapping("/ocr")
public class CardOcrController {


    @CrossOrigin
    @RequestMapping("/test")
    public JSONObject getByJSON(@RequestBody JSONObject jsonParam) {
        System.out.println("test Print");

        // 直接将json信息打印出来
        System.out.println(jsonParam.toJSONString());

        // 将获取的json数据封装一层，然后在给返回
        JSONObject result = new JSONObject();
        result.put("msg", "ok");
        result.put("method", "json");
        result.put("data", jsonParam);

//        return result.toJSONString();
        return result;
    }


    @Autowired
    private ICardOcrService iCardOcrService;

    @CrossOrigin
    @RequestMapping(value = "/idCardOcr", produces = "application/json;charset=utf-8")
    @ResponseBody
    public IDCard idCardOcr(@RequestParam("file") MultipartFile file) {
        long startTime = System.currentTimeMillis();
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (Exception e) {
            System.out.println(e);
        }

        if (bufferedImage != null) {
            IDCard idCard = iCardOcrService.cardOcr(bufferedImage);
            long endTime = System.currentTimeMillis();
            if (idCard != null) {
                System.out.println("识别时间：" + (endTime - startTime));
                return idCard;
            } else
                return null;
        } else
            return null;
    }

}
