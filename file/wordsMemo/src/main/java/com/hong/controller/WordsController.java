/**
 *
 */
package com.hong.controller;

import com.google.gson.Gson;
import com.hong.entity.WordEntity;
import com.hong.service.WordService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hong.youdao.api.ConsultFromYoudao;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cmss-hong
 *
 */
@RestController
//@RequestMapping("/")
public class WordsController {

    private static Logger logger = LoggerFactory.getLogger(WordsController.class);
    @Autowired
    private ConsultFromYoudao consultFromYoudao;

    @Autowired
    private WordService wordService;

    @RequestMapping("/")
    public String index() {
        return "Just go ahead!";
    }

    @RequestMapping("/word/{word}")
    public String getTranslate(@PathVariable("word") String word) {
        if (StringUtils.isNotBlank(word)) {
            word = word.trim().toLowerCase();
            WordEntity wordEntity = new WordEntity();
            wordEntity.setWord(word);
            List<WordEntity> list = wordService.searchDatabase(wordEntity);
            if (list != null && list.size() > 0) {
//                Gson gson = new Gson();
//                return gson.toJson(list.get(0));
                return list.get(0).getMeaning();
            }
            try {
                String meaning = consultFromYoudao.consult(word);
                // 只保存英文单词，中文不予保存
                if (word.matches("^[a-zA-Z]*")) {
                    WordEntity newWord = new WordEntity();
                    newWord.setWord(word);
                    newWord.setMeaning(meaning);
                    wordService.save(newWord);
                }
                return meaning;
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        return "error occurs.";
    }
}
