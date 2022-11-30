package com.dw.drone.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dw.drone.mapper.MainMapper;
import com.dw.drone.vo.JoinVO;

@Service
public class MainService {
	@Autowired
	MainMapper mapper;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	
	public int Join(JoinVO vo) {
		
		String password = vo.getUserPassword(); // 사용자 비밀번호를 가져옴
		password = passwordEncoder.encode(password); //암호화
		vo.setUserPassword(password);
		
		//passwordEncoder.matches(password, password); : 암호 해독! 사용자가 입력한 비밀번호와 디비에 저장된 비밀번호를 일치여부 체크
		return mapper.insertUsers(vo);
	}

	public boolean verifyRecaptcha(String recaptcha) {
		final String SECRET_KEY = "6Lf5nYwiAAAAABrWJNB6PB43KAEPkKaAeq7hUfSP";
		final String RE_URL = "https://www.google.com/recaptcha/api/siteverify";//리캡차 인증 주소
		try {
			URL obj = new URL(RE_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");

			String postParams = "secret=" + SECRET_KEY + "&response=" + recaptcha;
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			return jsonObject.getBoolean("success"); // true or false

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
