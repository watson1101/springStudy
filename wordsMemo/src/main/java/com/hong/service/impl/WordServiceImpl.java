package com.hong.service.impl;

import com.hong.entity.WordEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.hong.dao.WordDao;
import com.hong.service.WordService;
import org.springframework.stereotype.Service;

@Service
public class WordServiceImpl implements WordService {
	@Autowired
	private WordDao wordDao;

	@Override
	public List<WordEntity> searchDatabase(WordEntity word){
		List<WordEntity> list = wordDao.searchWord(word);
		return list;
	}

	@Override
	public void save(WordEntity word){
		wordDao.save(word);
	}

	@Override
	public void addSearchCount(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPositiveCount(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addnegativeCount(int id) {
		// TODO Auto-generated method stub

	}

}
