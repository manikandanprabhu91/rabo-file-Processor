package com.rabo.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rabo.assignment.pojo.RaboRecordResponse;
import com.repo.assignment.utils.RaboUtil;

@Service
public class RaboService {
	
	@Autowired
	RaboUtil raboUtil;
	
	public RaboRecordResponse dataProcessor(MultipartFile multipartFile) throws Exception {
		RaboRecordResponse recordResponse = raboUtil.loadAndValidateData(multipartFile);
		return recordResponse;
	}

}
