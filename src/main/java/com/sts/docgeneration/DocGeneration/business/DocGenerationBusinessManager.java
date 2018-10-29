package com.sts.docgeneration.DocGeneration.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sts.docgeneration.DocGeneration.domain.DBFile;
import com.sts.docgeneration.DocGeneration.exception.MyFileNotFoundException;
import com.sts.docgeneration.DocGeneration.repository.DBFileRepository;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;
@Service
public class DocGenerationBusinessManager {

	@Autowired
	private DBFileRepository dbFileRepository;

	//Save the uploaded file to this folder
	private static String UPLOADED_FOLDER = "D:\\template\\";

	/**
	 * create document based template ID
	 * @param payloaOd
	 * @return
	 */
	public byte[] generateDocument(String payloaOd) {
		// preparing variables
		Variables variables = new Variables();

		// TODO Auto-generated method stub
		System.out.println(">>> >> >>"+payloaOd);

		JSONObject obj = new JSONObject(payloaOd);
		String templateID = obj.getString("templateID");

		Docx docx = new Docx("D:\\template\\"+templateID+".docx");
		docx.setVariablePattern(new VariablePattern("${", "}"));


		JSONObject jsonObject=obj.getJSONObject("variables");

		//save variables 
		Iterator<String> variablesKeys = jsonObject.keys();
		while(variablesKeys.hasNext()) {
			String variable = variablesKeys.next();
			variables.addTextVariable(new TextVariable("${"+variable+"}", jsonObject.get(variable).toString()));
		}


		// fill template
		docx.fillTemplate(variables);


		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 


		// save filled .docx file
		docx.save("docGenerate.docx");
		docx.save(outputStream);

		return outputStream.toByteArray();

	}

	//save file
	public void saveUploadedFiles(List<MultipartFile> files) throws IOException {

		for (MultipartFile file : files) {

			if (file.isEmpty()) {
				continue; //next pls
			}


			String fileName = StringUtils.cleanPath(file.getOriginalFilename());

			System.out.println(">> >> >> >> file "+file.getContentType());
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);
			DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());
			dbFileRepository.save(dbFile);

		}
	}

	public DBFile getFile(String fileId) {
		return dbFileRepository.findByFileName(fileId);
				
	}
	
	public void deleteFile(String fileId) {
		 dbFileRepository.deleteById(fileId);
				
	}

	public  List<DBFile> getAllTemplates() {

		List<DBFile>templats=dbFileRepository.findAll();

		return templats;

	}
}
