package com.hong.service;

import com.hong.entity.WordEntity;
import java.util.List;

/**
 * @author cmss-hong
 */
public interface WordService {

    public List<WordEntity> searchDatabase(WordEntity word);

    public void save(WordEntity word);

    public void addSearchCount(int id);

    public void addPositiveCount(int id);

    public void addnegativeCount(int id);
}
