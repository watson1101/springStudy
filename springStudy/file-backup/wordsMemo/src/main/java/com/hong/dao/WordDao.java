package com.hong.dao;

import com.hong.domain.WordEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface WordDao {

    List<WordEntity> searchWord(WordEntity word);

    void save(WordEntity word);
}
