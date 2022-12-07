package com.example.demo.service.Impl;

import com.example.demo.entity.IDCard;
import com.example.demo.service.ICardOcrService;
import com.example.demo.utils.IDCardOcrUtil;
import com.example.demo.utils.ImageProcessUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

import java.util.List;

/**
 * @author ly
 * @since 2021/5/8
 */
@Service("iCardOcrService")
@ComponentScan
public class CardOcrServiceImpl implements ICardOcrService {

    @Override
    public IDCard cardOcr(BufferedImage bufferedImage) {

        IDCard idCard = new IDCard();
        //调用工具类ImageProcessUtil对图片进行处理并将处理后的图片保存到lstBufferedImage中
        List<BufferedImage> lstBufferedImage = ImageProcessUtil.idCardProcess(bufferedImage);
        //调用工具类IDCardOcrUtil对处理后的图片进行OCR识别并将识别结果保存到lstIdCardInfo中
        List<Object> lstIdCardInfo = IDCardOcrUtil.idCardOcr(lstBufferedImage);

        if (lstIdCardInfo != null) {
            //将lstIdCardInfo中的身份证信息添加到身份证对象idCard中
            idCard.setName(lstIdCardInfo.get(0).toString());
            idCard.setSex((Character) lstIdCardInfo.get(1));
            idCard.setNation(lstIdCardInfo.get(2).toString());
            idCard.setYear((Integer) lstIdCardInfo.get(3));
            idCard.setMonth((Integer) lstIdCardInfo.get(4));
            idCard.setDay((Integer) lstIdCardInfo.get(5));
            idCard.setAddress(lstIdCardInfo.get(6).toString());
            idCard.setIdNumber(lstIdCardInfo.get(7).toString());
        } else {
            return null;
        }

        return idCard;
    }
}
