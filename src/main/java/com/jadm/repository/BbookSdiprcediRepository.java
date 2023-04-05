package com.jadm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jadm.model.BbookSdiprcedi;

@Repository
@Transactional
public interface BbookSdiprcediRepository extends CrudRepository<BbookSdiprcedi, Long> {
	

	List<BbookSdiprcedi> findByBatchNum(Long batchNum);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "update app_sam.bbook_sdiprcedi s set s.download_date_1 = sysdate where batch_num = :batchNum", nativeQuery = true)
    public void updSdiprcedi(@Param("batchNum") Long batchNum);		

}
