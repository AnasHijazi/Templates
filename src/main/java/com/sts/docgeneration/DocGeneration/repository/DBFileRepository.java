package com.sts.docgeneration.DocGeneration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sts.docgeneration.DocGeneration.domain.DBFile;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {

	
	 DBFile findByFileName(String fileName);
}
