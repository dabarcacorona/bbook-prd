package com.jadm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jadm.model.BbookMtcSdivpcpvr;
import com.jadm.model.BbookSdiprcedi;
import com.jadm.model.BbookSdiprcehi;
import com.jadm.model.BbookSdiprdati;
import com.jadm.model.BbookSdiprddsi;
import com.jadm.model.BbookSdiprddss;
import com.jadm.model.BbookSdiprdmsi;
import com.jadm.model.BbookSdivpccsi;
import com.jadm.repository.BbookRepository;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
@Transactional
public class BbookGeneralService {

	
	@Value("${sftp.ip}")
	private String sftpip;

	@Value("${sftp.prt}")
	private int sftpprt;
	
    @Value("${sftp.usr}")
	private String sftpusr;

    @Value("${sftp.pss}")
	private String sftppss;

    @Value("${sftp.dtn}")
	private String sftpdtn;

	@Value("${carpeta.general}")
	private String carpeta;
	
	@Value("${carpeta.bajada}")
	private String bajada;

	@Value("${carpeta.subida}")
	private String subida;
	
	@Value("${separador.carpetas}")
	private String separador;
	
	
	@Autowired
    private BbookSdiprcediService bbooksdiprcediservice;
	
	@Autowired
    private BbookSdiprcehiService bbooksdiprcehiservice;
	
	@Autowired
    private BbookSdiprdatiService bbooksdiprdatiservice;
	
	@Autowired
    private BbookSdiprddsiService bbooksdiprddsiservice;

	@Autowired
    private BbookSdiprddssService bbooksdiprddssservice;
	
	@Autowired
    private BbookSdiprdmsiService bbooksdiprdmsiservice;

	@Autowired
    private BbookSdivpccsiService bbooksdivpccsiservice;

	@Autowired
    private BbookMtcSdivpcpvrService bbookmtcsdivpcpvrservice;
	
	@Autowired
    private BbookRepository bbookrepository;	
	
	private static final Logger LOG = LoggerFactory.getLogger(BbookGeneralService.class);
	
	
	public void llamadaGenaral(Long idbatchnum) throws IOException {
		
		String strDir = System.getProperty("user.dir");
        LOG.info("strDir (user.dir) = ({})", strDir);
        
		   Date fecha = new Date(Calendar.getInstance().getTimeInMillis());
	       SimpleDateFormat formatterf = new SimpleDateFormat("yyyyMMdd");
	       SimpleDateFormat formatterh = new SimpleDateFormat("HHmmss");
	       final String nombreArchivoDescarga1 = "sdiprddsi_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";        
	       final String nombreArchivoDescarga2 = "sdiprdmsi_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";	       
	       final String nombreArchivoDescarga3 = "sdiprddss_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";	       
	       final String nombreArchivoDescarga4 = "sdiprdati_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";	       
	       final String nombreArchivoDescarga5 = "sdivpccsi_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";	       
	       final String nombreArchivoDescarga6 = "sdiprcehi_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";	       
	       final String nombreArchivoDescarga7 = "sdiprcedi_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";	       
	       final String nombreArchivoDescarga8 = "mtc_sdivpcpvr_"+formatterf.format(fecha)+formatterh.format(fecha)+".csv";
	       final String nombreArchivoDescarga9 = "mtcsdiprdmsi_"+formatterf.format(fecha)+formatterh.format(fecha)+".zip";
	       final String nombreArchivoDescarga10 = "MTCSDIPRDMSI_"+formatterf.format(fecha)+formatterh.format(fecha);	       
	       
	       final String pathCompletoArchivoResultanteDescarga = strDir+separador+carpeta+separador+bajada+separador;
           
	       List<String> estilolist = bbookrepository.RecEstilo(idbatchnum);
	       String estilo = estilolist.get(0).toString();
	       
	       List<BbookSdiprddsi> bbooksdiprddsiResult = bbooksdiprddsiservice.writeToCsvDetailCsv(nombreArchivoDescarga1, pathCompletoArchivoResultanteDescarga, idbatchnum);

	       List<BbookSdiprdmsi> bbooksdiprdmsiResult = bbooksdiprdmsiservice.writeToCsvDetailCsv(nombreArchivoDescarga2, pathCompletoArchivoResultanteDescarga, idbatchnum);
		   
	       long bbooksdiprddsssum = 0;
	       long bbookmtcsdivpcpvrsum = 0;
	       if (estilo.equals("T")) {
	           List<BbookSdiprddss> bbooksdiprddssResult = bbooksdiprddssservice.writeToCsvDetailCsv(nombreArchivoDescarga3, pathCompletoArchivoResultanteDescarga, idbatchnum);
	           List<BbookMtcSdivpcpvr> bbookmtcsdivpcpvrResult = bbookmtcsdivpcpvrservice.writeToCsvDetailCsv(nombreArchivoDescarga8, pathCompletoArchivoResultanteDescarga, idbatchnum);
	           bbooksdiprddsssum = bbooksdiprddssResult.size() + 1;
	           bbookmtcsdivpcpvrsum = bbookmtcsdivpcpvrResult.size() + 1;
	       }
	       List<BbookSdiprdati> bbooksdiprdatiResult = bbooksdiprdatiservice.writeToCsvDetailCsv(nombreArchivoDescarga4, pathCompletoArchivoResultanteDescarga, idbatchnum);
	       
	       List<BbookSdivpccsi> bbooksdivpccsiResult = bbooksdivpccsiservice.writeToCsvDetailCsv(nombreArchivoDescarga5, pathCompletoArchivoResultanteDescarga, idbatchnum);
		   	
	       List<BbookSdiprcehi> bbooksdiprcehiResult = bbooksdiprcehiservice.writeToCsvDetailCsv(nombreArchivoDescarga6, pathCompletoArchivoResultanteDescarga, idbatchnum);

	       List<BbookSdiprcedi> bbooksdiprcediResult = bbooksdiprcediservice.writeToCsvDetailCsv(nombreArchivoDescarga7, pathCompletoArchivoResultanteDescarga, idbatchnum);
	       
	       
	       
	      // Archivo de Contorl
	       
           long bbooksdiprddsisum = bbooksdiprddsiResult.size() + 1;
           long bbooksdiprdmsisum = bbooksdiprdmsiResult.size() + 1;
           long bbooksdiprdatisum = bbooksdiprdatiResult.size() + 1;
           long bbooksdivpccsisum = bbooksdivpccsiResult.size() + 1;
           long bbooksdiprcehisum = bbooksdiprcehiResult.size() + 1;
           long bbooksdiprcedisum = bbooksdiprcediResult.size() + 1;
           

           
           try {
               PrintWriter writer = new PrintWriter(pathCompletoArchivoResultanteDescarga + nombreArchivoDescarga10, "UTF-8");
               
               writer.println(nombreArchivoDescarga1+";"+bbooksdiprddsisum);
               writer.println(nombreArchivoDescarga2+";"+bbooksdiprdmsisum);
               if (estilo.equals("T")) {writer.println(nombreArchivoDescarga3+";"+bbooksdiprddsssum);}
               writer.println(nombreArchivoDescarga4+";"+bbooksdiprdatisum);
               writer.println(nombreArchivoDescarga5+";"+bbooksdivpccsisum);
               writer.println(nombreArchivoDescarga6+";"+bbooksdiprcehisum);
               writer.println(nombreArchivoDescarga7+";"+bbooksdiprcedisum);
               if (estilo.equals("T")) {writer.println(nombreArchivoDescarga8+";"+bbookmtcsdivpcpvrsum);}
               writer.close();
           } catch (Exception e) {
               e.printStackTrace();
           }		            
           
//Cear archivo Zip
           File f = new File(pathCompletoArchivoResultanteDescarga + nombreArchivoDescarga9 );
           ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
           
           Path xfile = null;
           ZipEntry ze = null;
           
           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga1);
           ze = new ZipEntry(nombreArchivoDescarga1);
           out.putNextEntry(ze);
           byte[] bytes = Files.readAllBytes(xfile); 
           out.write(bytes, 0, bytes.length);
           out.closeEntry();

           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga2);
           ze = new ZipEntry(nombreArchivoDescarga2);
           out.putNextEntry(ze);
           bytes = Files.readAllBytes(xfile); 
           out.write(bytes, 0, bytes.length);
           out.closeEntry();
           
           if (estilo.equals("T")) {
	           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga3);
	           ze = new ZipEntry(nombreArchivoDescarga3);
	           out.putNextEntry(ze);
	           bytes = Files.readAllBytes(xfile); 
	           out.write(bytes, 0, bytes.length);
	           out.closeEntry();
           } 

           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga4);
           ze = new ZipEntry(nombreArchivoDescarga4);
           out.putNextEntry(ze);
           bytes = Files.readAllBytes(xfile); 
           out.write(bytes, 0, bytes.length);
           out.closeEntry();
           
           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga5);
           ze = new ZipEntry(nombreArchivoDescarga5);
           out.putNextEntry(ze);
           bytes = Files.readAllBytes(xfile); 
           out.write(bytes, 0, bytes.length);
           out.closeEntry();
           
           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga6);
           ze = new ZipEntry(nombreArchivoDescarga6);
           out.putNextEntry(ze);
           bytes = Files.readAllBytes(xfile); 
           out.write(bytes, 0, bytes.length);
           out.closeEntry();
           
           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga7);
           ze = new ZipEntry(nombreArchivoDescarga7);
           out.putNextEntry(ze);
           bytes = Files.readAllBytes(xfile); 
           out.write(bytes, 0, bytes.length);
           out.closeEntry();
           
           if (estilo.equals("T")) {
	           xfile = Paths.get(pathCompletoArchivoResultanteDescarga+nombreArchivoDescarga8);
	           ze = new ZipEntry(nombreArchivoDescarga8);
	           out.putNextEntry(ze);
	           bytes = Files.readAllBytes(xfile); 
	           out.write(bytes, 0, bytes.length);
	           out.closeEntry();
           }   

           out.close();		  	       

	       JSch jsch = new JSch();
           try {
               Session session = jsch.getSession(sftpusr,sftpip, sftpprt);
               session.setConfig("PreferredAuthentications", "password");
               session.setConfig("StrictHostKeyChecking", "no");
               session.setPassword(sftppss);
               session.connect();
               Channel channel = session.openChannel("sftp");
               ChannelSftp sftp = (ChannelSftp) channel;
               sftp.connect();
             
              File file = new File(pathCompletoArchivoResultanteDescarga + nombreArchivoDescarga9);
              InputStream inputStream = new FileInputStream(file);
              sftp.cd(sftpdtn);
              sftp.put(inputStream,file.getName());
              inputStream.close();
              
              
              file = new File(pathCompletoArchivoResultanteDescarga + nombreArchivoDescarga10);
              inputStream = new FileInputStream(file);
              sftp.cd(sftpdtn);
              sftp.put(inputStream,file.getName());
              inputStream.close();
              
            } catch (JSchException e) {
            	LOG.error("No se pudo realizar la conexión ,{}",  e);	
            } catch (SftpException e) {
            	LOG.error("No se pudo subir el fichero ,{}",  e);	
            } catch (IOException e) {
            	LOG.error("No se pudo crear el fichero ,{}",  e);	
            }
          	   
	   	   }	
	
}
