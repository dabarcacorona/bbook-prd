package com.jadm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jadm.model.BbookJsonPrd;


@Repository
public interface BbookRepository extends CrudRepository <BbookJsonPrd, Long> {


	@Modifying(clearAutomatically = true)
	@Query(value = "select decode(a.value, 'Si', 'T', 'F') estilo from BBOOK_JSON_PRD t, JSON_TABLE(t.bk_json, '$.data[*]' columns nested path '$.dim_values[*]' columns(id_color  varchar2(6) path '$.id',nested path '$.gs_attrs[*]' columns(code varchar2(100) path '$.code', value varchar2(15) path '$.value')))a where BK_ID = :batchNum and  a.code in ('ESTILO') ", nativeQuery = true)
    public List<String> RecEstilo(@Param("batchNum") long batchNum);
	
}
