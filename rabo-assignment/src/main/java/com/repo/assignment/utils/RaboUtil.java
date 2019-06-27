package com.repo.assignment.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.rabo.assignment.pojo.RaboRecordResponse;
import com.rabo.assignment.pojo.RaboResponse;
import com.rabo.assignment.pojo.Record;
import com.rabo.assignment.pojo.Records;
import com.repo.assignment.constant.RaboConstatnt;

@Component
public class RaboUtil {

	private final Logger logger = Logger.getLogger(RaboUtil.class.getName());

	public RaboRecordResponse loadAndValidateData(MultipartFile file) throws IOException {
		List<Record> recordList = new ArrayList<>();
		RaboRecordResponse reboResponse = null;
		String fileType = "";
		String[] extensionArray = file.getOriginalFilename().split("\\.");
		String extension = extensionArray.length > 0 ? extensionArray[1] : null;

		// Validate INput FIle Format
		if (file.isEmpty() || "xml".equalsIgnoreCase(extension)) {
			fileType = "xml";
		} else if (file.isEmpty() || "csv".equalsIgnoreCase(extension)) {
			fileType = "csv";
		}
		else if (file.isEmpty() || !"csv".equalsIgnoreCase(extension) || !"xml".equalsIgnoreCase(extension)) {
			logger.info("Invalid Input File.Please Upload csv file with extension (.csv) or xml file with extension (.xml)"); 
			reboResponse = new RaboRecordResponse();
			reboResponse.setMessage("Invalid Input File.Please Upload csv file with extension (.csv)");
			return reboResponse;
		}
		logger.info("::::::::::loadAndValidateData csvparser start ::::::::::::");
		try {
			if(fileType.equals(RaboConstatnt.CSV)) {
				CSVParser csvParser = CSVFormat.EXCEL.withHeader().
						parse(new InputStreamReader(file.getInputStream()));
				logger.info("::::::::::loadAndValidateData csvparser end ::::::::::::");
				for(CSVRecord csvRecord : csvParser) {
					Record record = new Record();
					record.setRefernce(Integer.valueOf(csvRecord.get(RaboConstatnt.REFERENCE)));
					record.setAccountNumber(csvRecord.get(RaboConstatnt.ACCOUNT_NUMBER));
					record.setDescription(csvRecord.get(RaboConstatnt.DESCRIPTION));
					record.setStartBalance(csvRecord.get(RaboConstatnt.START_BALANCE));
					record.setMutation(csvRecord.get(RaboConstatnt.MUTATION));
					record.setEndBalance(csvRecord.get(RaboConstatnt.END_BALANCE));
					recordList.add(record);
					reboResponse = ValidateRecord(recordList);
					reboResponse.setMessage("Successfully proccessed");
				}
			} else if(fileType.equals(RaboConstatnt.XML)) {
				JAXBContext jaxbContext = JAXBContext.newInstance(Records.class);
	            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	            Records records = (Records) jaxbUnmarshaller.unmarshal(file.getInputStream());
	            reboResponse = ValidateRecord(records.getRecord());
				reboResponse.setMessage("Successfully proccessed");
			}
		}catch (Exception e) {
			logger.log(Level.WARNING, ":::::::::::::::::loadAndValidateData failure issue:::::::: " + e.getMessage());
			reboResponse = new RaboRecordResponse();
			reboResponse.setMessage("Reborecord is failed");
		}

		return reboResponse;
	}

	@SuppressWarnings("unlikely-arg-type")
	protected RaboRecordResponse ValidateRecord(List<Record> recordList) {
		List<RaboResponse> reboResponse = new ArrayList<>();
		Map<String, String> recordMap = new HashMap<>();
		Map<String, String> balanceFailureMap = new HashMap<>();
		try {
			for (Record record : recordList) {
				RaboResponse repoResponse = null;
				BigDecimal startbal = BigDecimal.valueOf(Double.valueOf(record.getStartBalance())); 
				BigDecimal midbal = BigDecimal.valueOf(Double.valueOf(record.getMutation()));
				BigDecimal total = startbal.add(midbal);
				BigDecimal endbalance =  BigDecimal.valueOf(Double.valueOf(record.getEndBalance()));
				if(endbalance != total) {
					repoResponse = setRepoRecordResponse(record);
					balanceFailureMap.put(String.valueOf(record.getRefernce()), String.valueOf(record.getRefernce()));
				}
				if(recordMap.size() == 0)
					recordMap.put(String.valueOf(record.getRefernce()), String.valueOf(record.getRefernce()));
				else if(recordMap.size() >= 0 && !recordMap.containsKey(record.getRefernce())) {
					recordMap.put(String.valueOf(record.getRefernce()), String.valueOf(record.getRefernce()));
				}else if(recordMap.size() >= 0 && recordMap.containsKey(record.getRefernce())) {

					if(balanceFailureMap.size() >=0 && !balanceFailureMap.containsKey(record.getRefernce())) {
						repoResponse = setRepoRecordResponse(record);
					} else {
						repoResponse = setRepoRecordResponse(record);
					}
					reboResponse.add(repoResponse);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, ":::::::::::::::::ValidateRecord failure issue:::::::: " + e.getMessage());
		}
		RaboRecordResponse recordResponse = new RaboRecordResponse();
		recordResponse.setReboResponse(reboResponse);
		return recordResponse;
	}

	private RaboResponse setRepoRecordResponse(Record record) {
		RaboResponse reboResponse = new RaboResponse();
		reboResponse.setRefernce(String.valueOf(record.getRefernce()));
		reboResponse.setEndBalance(record.getEndBalance());
		reboResponse.setDescription(record.getDescription());
		return reboResponse;
	}

}
