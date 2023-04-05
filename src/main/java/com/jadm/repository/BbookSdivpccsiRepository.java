package com.jadm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jadm.model.BbookSdivpccsi;


@Repository
@Transactional
public interface BbookSdivpccsiRepository extends CrudRepository<BbookSdivpccsi, Long>{
	
	List<BbookSdivpccsi> findByDownloadDate1IsNullAndBatchNum(long batchNum);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "update app_sam.bbook_sdivpccsi s set s.download_date_1 = sysdate where batch_num = :batchNum", nativeQuery = true)
    public void updSdivpccsi(@Param("batchNum") long batchNum);		

}
