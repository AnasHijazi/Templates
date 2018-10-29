package com.sts.docgeneration.DocGeneration.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sts.docgeneration.DocGeneration.business.DocGenerationBusinessManager;
import com.sts.docgeneration.DocGeneration.domain.ApplicationResponse;
import com.sts.docgeneration.DocGeneration.domain.DBFile;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

@RestController
@RequestMapping("/")
public class DocGenerationController {
	private final Logger logger = LoggerFactory.getLogger(DocGenerationController.class);

	@Autowired
	DocGenerationBusinessManager docGenerationBusinessManager;

	/**
	 * generate document pass variables 
	 * @param payloaOd
	 * @return
	 */
	@RequestMapping(value=DocGenerationURL_Constant.GENERATE_DOC, produces = "application/json; charset=utf-8")
	ResponseEntity<ApplicationResponse> generateDocument(@RequestBody String payloaOd){

		byte[] data=docGenerationBusinessManager.generateDocument(payloaOd);
		if(data!=null) {

			return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(true, 
					"SUCCESS_OPERATION","Document created successfully", 
					data),
					HttpStatus.OK);

		}else {
			return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(false, 
					"Faild_OPERATION","Document created Unsuccessfully", 
					data),
					HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * upload Word document template 
	 * @param uploadfile
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value=DocGenerationURL_Constant.UPLOAD_DOC,method = RequestMethod.POST)
	public ResponseEntity<?> uploadFile(
			@RequestParam("file") MultipartFile uploadfile) {

		logger.debug("Single file upload!");

		if (uploadfile.isEmpty()) {
			return new ResponseEntity("please select a file!", HttpStatus.BAD_REQUEST);
		}

		String fileExtentions = ".doc,.docx";
		String fileName = uploadfile.getOriginalFilename();
		int lastIndex = fileName.lastIndexOf('.');
		String substring = fileName.substring(lastIndex, fileName.length());

		if (!fileExtentions.contains(substring)) {
			//logic
			return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(false, 
					"File Not Word Document - " +uploadfile.getOriginalFilename(),"Document Uploaded Unsuccessfully", 
					null),
					HttpStatus.BAD_REQUEST);
		}
		try {

			docGenerationBusinessManager.saveUploadedFiles(Arrays.asList(uploadfile));

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(true, 
				"Successfully uploaded - " +uploadfile.getOriginalFilename(),"Document Uploaded successfully", 
				null),
				HttpStatus.OK);

	}

	
	/**
	 * Retrieve Template By Id 
	 * @param fileId
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value=DocGenerationURL_Constant.GET_TEMPLATE)
	private ResponseEntity<ByteArrayResource> getTemplate(@PathVariable String fileId) {
		// TODO Auto-generated method stub
		// Load file from database
		DBFile dbFile = docGenerationBusinessManager.getFile(fileId);

		
		if(dbFile!=null) {
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(dbFile.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
				.body(new ByteArrayResource(dbFile.getData()));
		}else {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	/**
	 * Retrieve Template By Id 
	 * @param fileId
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value=DocGenerationURL_Constant.GET_TEMPLATE,method = RequestMethod.DELETE)
	private ResponseEntity<ApplicationResponse> deleteTemplateById(@PathVariable String fileId) {
		// TODO Auto-generated method stub
		// Load file from database
		docGenerationBusinessManager.deleteFile(fileId);


		return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(true, 
				"SUCCESS_OPERATION","Document Deleted successfully", 
				fileId),
				HttpStatus.OK);


	}

	@CrossOrigin
	@RequestMapping(value=DocGenerationURL_Constant.GET_ALL_TEMPLATE)
		private ResponseEntity<ApplicationResponse> getAllTemplates() {
		
		List<DBFile>templates=docGenerationBusinessManager.getAllTemplates();
		if(templates!=null) {

			return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(true, 
					"SUCCESS_OPERATION","Document created successfully", 
					templates),
					HttpStatus.OK);

		}else {
			return new ResponseEntity<ApplicationResponse>(new ApplicationResponse(false, 
					"Faild_OPERATION","", 
					null),
					HttpStatus.BAD_REQUEST);
		}
		}


	public static void main(String[] args) {


		Docx docx = new Docx("D:\\temp\\template.docx");
		docx.setVariablePattern(new VariablePattern("${", "}"));


		// preparing variables
		Variables variables = new Variables();



		variables.addTextVariable(new TextVariable("${firstName}", "Anas"));
		variables.addTextVariable(new TextVariable("${lastName}", "Hijazi"));


		/*	TableVariable tableVariable = new TableVariable();
		 List<Variable> nameColumnVariables = new ArrayList<Variable>();
		    List<Variable> ageColumnVariables = new ArrayList<Variable>();
		    List<Variable> logoColumnVariables = new ArrayList<Variable>();
		    List<Variable> languagesColumnVariables = new ArrayList<Variable>();

		    nameColumnVariables.add(new TextVariable("${name}", "ALI"));
		    ageColumnVariables.add(new TextVariable("${age}", "15"));
		      logoColumnVariables.add(new TextVariable("${logo}", "x.jpeg"));
		    languagesColumnVariables.add(new TextVariable("${lang}", "English"));

		    tableVariable.addVariable(nameColumnVariables);



		variables.addTableVariable(tableVariable);*/
		// fill template
		docx.fillTemplate(variables);

		// save filled .docx file
		docx.save("docGenerate.docx");
	}
}
