package com.example.demo.service.Impl;

import com.example.demo.entity.IDCard;
import com.example.demo.service.ICardOcrService;
import com.example.demo.utils.IDCardOcrUtil;
import com.example.demo.utils.ImageProcessUtil;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * @author ly
 * @since 2021/5/8
 */
@Service("iUserService")
public class CardOcrServiceImpl implements ICardOcrService {
    @Override
    public IDCard cardOcr(BufferedImage bufferedImage) {

        IDCard idCard = new IDCard();
//        //原图路径
//        String sourceImage = "E:\\Desktop\\OCRTest\\image\\01.png";
//        BufferedImage bufferedImage = null;
//        try {
//            bufferedImage = ImageIO.read(new File(sourceImage));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<BufferedImage> lstBufferedImage = ImageProcessUtil.idCardProcess(bufferedImage);

        List<Object> lstIdCardInfo = IDCardOcrUtil.idCardOcr(lstBufferedImage);

        idCard.setName(lstIdCardInfo.get(0).toString());
        idCard.setSex((Character) lstIdCardInfo.get(1));
        idCard.setNation(lstIdCardInfo.get(2).toString());
        idCard.setYear((Integer) lstIdCardInfo.get(3));
        idCard.setMonth((Integer) lstIdCardInfo.get(4));
        idCard.setDay((Integer) lstIdCardInfo.get(5));
        idCard.setAddress(lstIdCardInfo.get(6).toString());
        idCard.setIdNumber(lstIdCardInfo.get(7).toString());

        return idCard;
    }


}
