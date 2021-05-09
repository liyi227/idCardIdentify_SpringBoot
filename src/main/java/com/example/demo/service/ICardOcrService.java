package com.example.demo.service;

import com.example.demo.entity.IDCard;

import java.awt.image.BufferedImage;
import java.util.Base64;

/**
 * @author ly
 * @since 2021/5/8
 */
public interface ICardOcrService {
    IDCard cardOcr(BufferedImage bufferedImage);
}
