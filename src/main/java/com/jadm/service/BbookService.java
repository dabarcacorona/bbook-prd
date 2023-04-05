package com.jadm.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadm.model.BbookJsonPrd;
import com.jadm.repository.BbookRepository;

@Service
public class BbookService {

	@Autowired
	BbookRepository bbookrepository;
	
	public ArrayList<BbookJsonPrd> obtenerJsob(){
		
		return (ArrayList<BbookJsonPrd>) bbookrepository.findAll();
	}
	
	public BbookJsonPrd guardarJsob(BbookJsonPrd bbookjson){
		
		return bbookrepository.save(bbookjson);
	}
	
	public Optional<BbookJsonPrd> buscarJsob(Long idbk){
		
		return bbookrepository.findById(idbk);
	}
}
