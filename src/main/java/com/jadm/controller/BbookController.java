package com.jadm.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jadm.model.BbookJsonPrd;
import com.jadm.repository.BbookMtcSdivpcpvrRepository;
import com.jadm.repository.BbookSdiprcediRepository;
import com.jadm.repository.BbookSdiprcehiRepository;
import com.jadm.repository.BbookSdiprdatiRepository;
import com.jadm.repository.BbookSdiprddsiRepository;
import com.jadm.repository.BbookSdiprddssRepository;
import com.jadm.repository.BbookSdiprdmsiRepository;
import com.jadm.repository.BbookSdivpccsiRepository;
import com.jadm.repository.BbookSpInvoke;
import com.jadm.service.BbookGeneralService;
import com.jadm.service.BbookSdivpccsiService;
import com.jadm.service.BbookService;


@RestController
@RequestMapping("/api/CreaPRD")
public class BbookController {

		private static final Logger LOG = LoggerFactory.getLogger(BbookController.class);
		
		@Value("${tokken}")
		private String tokken;
		
	
		@Autowired
		private BbookService bbookservice;

		@Autowired
		private BbookSpInvoke bbookspinvoke;
		
		@Autowired
		private BbookGeneralService bbookgeneralservice;
		
		@Autowired
	    private BbookSdiprcediRepository bbooksdiprcedirepository;
		
		@Autowired
	    private BbookSdiprcehiRepository bbooksdiprcehirepository;
		
		@Autowired
	    private BbookSdiprdatiRepository bbooksdiprdatirepository;
		
		@Autowired
	    private BbookSdiprddsiRepository bbooksdiprddsirepository;

		@Autowired
	    private BbookSdiprddssRepository bbooksdiprddssrepository;
		
		@Autowired
	    private BbookSdiprdmsiRepository bbooksdiprdmsirepository;
		
		@Autowired
	    private BbookSdivpccsiRepository bbooksdivpccsirepository;		
		
		@Autowired
	    private BbookMtcSdivpcpvrRepository bbookmtcsdivpcpvrrepository;		

		@Autowired
		private EntityManager entityManager;



		@PostMapping()
		@ResponseBody
		@Produces(MediaType.APPLICATION_JSON)
		@Consumes(MediaType.APPLICATION_JSON)
	    //public Optional<BbookJson> guardarJsob(@RequestBody String StrBbookJson, @RequestHeader(value="Authorization") String token) throws IOException{
	    public String  guardarJsob(@RequestBody String StrBbookJson, @RequestHeader(value="Authorization") String token) throws IOException{
			String response;
			if(token == null) {
				
				return "{\"status\":\"Err\",\"statusCode\":401,\"internalCode\":\"00\",\"message\":\"Unauthorized\",\"alert\":\"Llamada sin token \"}";
			}
			
			if(token.equals(tokken)) {
				
			BigDecimal id_bk = (BigDecimal) entityManager.createNativeQuery("select bbook_number.nextval from dual").getSingleResult();
			BbookJsonPrd bbookjson = new BbookJsonPrd(id_bk.longValue(),StrBbookJson, null);
			
			try {
			   bbookservice.guardarJsob(bbookjson);
	           }catch (Exception e) {			
				LOG.debug("{} Err al guardar json ", e);
				return "{\"status\":\"Err\",\"statusCode\":000,\"internalCode\":\"01\",\"message\":\"Error al guardar json\",\"alert\":\"Error al guardar json\"}";
			}   
			
			try {
			   bbookspinvoke.CallSp(id_bk.longValue());
			}catch (Exception e) {
				LOG.debug("{} El Proceso Almacenado BBOOK CARGA PRD con error", e);
				return "{\"status\":\"Err\",\"statusCode\":000,\"internalCode\":\"02\",\"message\":\"Error sp carga oc\",\"alert\":\"Error sp carga oc\"}";			
			}
			
			try {
	        	bbookgeneralservice.llamadaGenaral(id_bk.longValue());
	        	
	        } catch (InvalidDataAccessResourceUsageException e) {
	        	LOG.error("{}: Ocurrio un error al momento de enviar las PRD: ", e );
				return "{\"status\":\"Err\",\"statusCode\":000,\"internalCode\":\"03\",\"message\":\"Error al enviar oc a pmm\",\"alert\":\"Error al enviar oc a pmm\"}";			
	        }
			
			bbooksdiprcedirepository.updSdiprcedi(id_bk.longValue());
			bbooksdiprcehirepository.updSdiprcehi(id_bk.longValue());
			bbooksdiprdatirepository.updSdiprdati(id_bk.longValue());
			bbooksdiprddsirepository.updSdiprddsi(id_bk.longValue());
			bbooksdiprddssrepository.updSdiprddss(id_bk.longValue());
			bbooksdiprdmsirepository.updSdiprdmsi(id_bk.longValue());
		    bbooksdivpccsirepository.updSdivpccsi(id_bk.longValue());
		    bbookmtcsdivpcpvrrepository.updMtcSdivpcpvr(id_bk.longValue());
			
		
	        return "{\"status\":\"OK\",\"statusCode\":201,\"internalCode\":\"00\",\"message\":\"Created\",\"alert\":\"Solicitud de oc creada\"}";
			
		} else {
			
			return "{\"status\":\"Err\",\"statusCode\":403,\"internalCode\":\"00\",\"message\":\"Forbidden\",\"alert\":\"Llamada con token invalido\"}";
		}
			
	}	
	

}
