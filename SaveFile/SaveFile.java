package ;

import java.io.FileOutputStream;
import org.springframework.web.multipart.MultipartFile;

public class SaveFile {								//관리자테이블에도 저장시 2개 다 사용
	public static void saveFile(MultipartFile mf, String admindir, String custdir) {
		byte [] data;
		String imgname = mf.getOriginalFilename();
		try {
			data = mf.getBytes();
			FileOutputStream fo = 
					new FileOutputStream(admindir+imgname);
			fo.write(data);
			fo.close();
			FileOutputStream fo2 = 
					new FileOutputStream(custdir+imgname);
			fo2.write(data);
			fo2.close();
		}catch(Exception e) {
			
		}
		
	}
	
}




