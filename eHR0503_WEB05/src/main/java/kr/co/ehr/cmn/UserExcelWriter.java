package kr.co.ehr.cmn;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import kr.co.ehr.user.service.User;

/*
 * user.xls -> FileWriter로 파일에 기록.
*.xls	97 ~ 2003버전			
			256컬럼	65535 Row
*.xlsx	2007이후 버전			
			16384컬럼	1,048,567 Row
			
*.xls	HSSWorkbook->HSSSheet->HSSRow->HSSCell			
*.xlsx	XSSWorkbook->XSSSheet->XSSRow->XSSCell			
*.csv				
			
 */
@Component
public class UserExcelWriter {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	
	public String csvWriterGeneralization(List<User> list, List<String> headers) {
		String fileFullPath = "";
		String path = StringUtil.dynamicDir();
		String excelFileNm = StringUtil.getUUID();
		String ext = ".csv";
		fileFullPath = path + File.separator + excelFileNm + ext;
		LOG.debug("=========================");
		LOG.debug("fileFullPath=" + fileFullPath);
		LOG.debug("=========================");

		FileWriter writer = null;
		try {
			//
			writer = new FileWriter(new File(fileFullPath));
			StringBuilder sb = new StringBuilder();
			// Header ID,이름,비번,Level,....
			String comma = ",";
			String lineSkip = "\n";
			
			for(int i=0;i<headers.size();i++) {
				String header = headers.get(i);
				sb.append(header);
				if (i != (headers.size() - 1)) {
					sb.append(comma);
				}
			}
			sb.append(lineSkip);
			// Header생성
			writer.write(sb.toString());

			// Data
			Object dto;

			for (int i = 0; i < list.size(); i++) {
				StringBuilder sbData = new StringBuilder();
				dto = list.get(i);
				Object obj = dto;

				Field[] fileds = obj.getClass().getDeclaredFields();
				for (int j = 0; j < fileds.length; j++) {
					Field field = fileds[j];

					field.setAccessible(true);

					try {
						Object value = field.get(obj);

						sbData.append(value.toString());
						if (j != (fileds.length - 1)) {
							sbData.append(comma);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				}
				
				// 마지막 라인 스킵 제거 !
				if (i != (list.size() - 1)) {
					sbData.append(lineSkip);
				}
				writer.write(sbData.toString());

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return fileFullPath;
	}	
	/**
	 * 
	 * @param list
	 * @return
	 */
	public String xlsxWriterGeneralization(List<User> list, List<String> headers) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		// 워크Sheet생성
		XSSFSheet sheet = workbook.createSheet();
		// 행생성
		XSSFRow row = sheet.createRow(0);
		// Cell생성
		XSSFCell cell;
		// header style
		// background color
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
		XSSFColor grey = new XSSFColor(new Color(192, 192, 192), colorMap);
		headerStyle.setFillForegroundColor(grey);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// 가운데 정열
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// 테두리
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);

		// Font설정
		Font headerFont = workbook.createFont();
		headerFont.setFontName("나눔고딕");
		headerFont.setFontHeight((short) (13 * 20));// font size
		headerStyle.setFont(headerFont);
		// 헤더정보 생성
		for (int i = 0; i < headers.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(headers.get(i));
			cell.setCellStyle(headerStyle);
		}

		// data style
		// background color
		XSSFCellStyle dataStyle = workbook.createCellStyle();
		XSSFCellStyle dataStyleL = workbook.createCellStyle();
		XSSFCellStyle dataStyleR = workbook.createCellStyle();

		// 가운데 정열
		dataStyle.setAlignment(HorizontalAlignment.CENTER);
		dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		dataStyleL.setAlignment(HorizontalAlignment.LEFT);
		dataStyleR.setAlignment(HorizontalAlignment.RIGHT);

		// 테두리
		dataStyle.setBorderTop(BorderStyle.THIN);
		dataStyle.setBorderBottom(BorderStyle.THIN);
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);

		dataStyleL.setBorderTop(BorderStyle.THIN);
		dataStyleL.setBorderBottom(BorderStyle.THIN);
		dataStyleL.setBorderLeft(BorderStyle.THIN);
		dataStyleL.setBorderRight(BorderStyle.THIN);

		dataStyleR.setBorderTop(BorderStyle.THIN);
		dataStyleR.setBorderBottom(BorderStyle.THIN);
		dataStyleR.setBorderLeft(BorderStyle.THIN);
		dataStyleR.setBorderRight(BorderStyle.THIN);

		// Font설정
		Font dataFont = workbook.createFont();
		dataFont.setFontName("나눔고딕");
		dataFont.setFontHeight((short) (12 * 20));// font size

		dataStyle.setFont(dataFont);
		dataStyleL.setFont(dataFont);
		dataStyleR.setFont(dataFont);
		// 데이터 생성
		Object dto;
		for (int i = 0; i < list.size(); i++) {
			dto = list.get(i);

			row = sheet.createRow(i + 1);
			Object obj = dto;

			Field[] fileds = obj.getClass().getDeclaredFields();
			for (int j = 0; j < fileds.length; j++) {
				Field field = fileds[j];

				field.setAccessible(true);

				try {
					Object value = field.get(obj);
					cell = row.createCell(j);

					LOG.debug("getType->" + field.getType());
					// DataType별 처리
					if (field.getType() == Integer.TYPE) {
						cell.setCellStyle(dataStyleR);
						cell.setCellValue(Integer.parseInt(value.toString()));
					} else if (field.getType() == Long.TYPE) {
						cell.setCellValue(Long.parseLong(value.toString()));
						cell.setCellStyle(dataStyleR);
					} else {
						cell.setCellValue(value.toString());
						cell.setCellStyle(dataStyleL);
					}

					
					LOG.debug(field.getName() + ":" + value);

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}

		// 02.년월 디렉토리 생성:D:\\HR_FILE\2019\09
		String path = StringUtil.dynamicDir();
		String excelFileNm = StringUtil.getUUID();
		String ext = ".xlsx";
		String fileFullPath = path + File.separator + excelFileNm + ext;
		LOG.debug("=========================");
		LOG.debug("fileFullPath=" + fileFullPath);
		LOG.debug("=========================");
		// File Write
		File file = new File(fileFullPath);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			// workbook -> FileOutputStream
			workbook.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				if (null != workbook) {
					workbook.close();
				}

				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileFullPath;
	}

	/**
	 * POI with style
	 * 
	 * @param list
	 * @return
	 */
	public String xlsxWriterStyle(List<User> list) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		// 워크Sheet생성
		XSSFSheet sheet = workbook.createSheet();
		// 행생성
		XSSFRow row = sheet.createRow(0);
		// Cell생성
		XSSFCell cell;

		// header style
		// background color
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
		XSSFColor grey = new XSSFColor(new Color(192, 192, 192), colorMap);
		headerStyle.setFillForegroundColor(grey);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// 가운데 정열
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// 테두리
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);

		// Font설정
		Font headerFont = workbook.createFont();
		headerFont.setFontName("나눔고딕");
		headerFont.setFontHeight((short) (13 * 20));// font size
		headerStyle.setFont(headerFont);

		// 헤더정보 생성
		cell = row.createCell(0);
		cell.setCellValue("ID");
		cell.setCellStyle(headerStyle);
		sheet.setColumnWidth(0, 100 * 50); // 100일때 0.2

		cell = row.createCell(1);
		cell.setCellValue("이름");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(2);
		cell.setCellValue("비번");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(3);
		cell.setCellValue("Level");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(4);
		cell.setCellValue("로그인");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(5);
		cell.setCellValue("추천");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(6);
		cell.setCellValue("이메일");
		cell.setCellStyle(headerStyle);
		sheet.setColumnWidth(6, 100 * 60); // 100일때 0.2

		cell = row.createCell(7);
		cell.setCellValue("등록일");
		cell.setCellStyle(headerStyle);
		sheet.setColumnWidth(7, 100 * 40); // 100일때 0.2
		// data style
		// background color
		XSSFCellStyle dataStyle = workbook.createCellStyle();
		XSSFCellStyle dataStyleL = workbook.createCellStyle();
		XSSFCellStyle dataStyleR = workbook.createCellStyle();

		// 가운데 정열
		dataStyle.setAlignment(HorizontalAlignment.CENTER);
		dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		dataStyleL.setAlignment(HorizontalAlignment.LEFT);
		dataStyleR.setAlignment(HorizontalAlignment.RIGHT);

		// 테두리
		dataStyle.setBorderTop(BorderStyle.THIN);
		dataStyle.setBorderBottom(BorderStyle.THIN);
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);

		dataStyleL.setBorderTop(BorderStyle.THIN);
		dataStyleL.setBorderBottom(BorderStyle.THIN);
		dataStyleL.setBorderLeft(BorderStyle.THIN);
		dataStyleL.setBorderRight(BorderStyle.THIN);

		dataStyleR.setBorderTop(BorderStyle.THIN);
		dataStyleR.setBorderBottom(BorderStyle.THIN);
		dataStyleR.setBorderLeft(BorderStyle.THIN);
		dataStyleR.setBorderRight(BorderStyle.THIN);

		// Font설정
		Font dataFont = workbook.createFont();
		dataFont.setFontName("나눔고딕");
		dataFont.setFontHeight((short) (12 * 20));// font size

		dataStyle.setFont(dataFont);
		dataStyleL.setFont(dataFont);
		dataStyleR.setFont(dataFont);

		// 데이터 생성
		User user;
		for (int i = 0; i < list.size(); i++) {
			user = new User();
			user = list.get(i);

			row = sheet.createRow(i + 1);

			cell = row.createCell(0);
			cell.setCellValue(user.getU_id());
			cell.setCellStyle(dataStyle);

			cell = row.createCell(1);
			cell.setCellValue(user.getName());
			cell.setCellStyle(dataStyle);

			cell = row.createCell(2);
			cell.setCellValue(user.getPasswd());
			cell.setCellStyle(dataStyle);

			cell = row.createCell(3);
			cell.setCellValue(user.gethLevel().toString());
			cell.setCellStyle(dataStyle);

			cell = row.createCell(4);
			cell.setCellValue(user.getLogin());
			cell.setCellStyle(dataStyle);

			cell = row.createCell(5);
			cell.setCellValue(user.getRecommend());
			cell.setCellStyle(dataStyle);
			dataStyle.setAlignment(HorizontalAlignment.RIGHT);

			cell = row.createCell(6);
			cell.setCellValue(user.getEmail());
			cell.setCellStyle(dataStyle);
			dataStyle.setAlignment(HorizontalAlignment.LEFT);

			cell = row.createCell(7);
			cell.setCellValue(user.getRegDt());
			cell.setCellStyle(dataStyle);
			dataStyle.setAlignment(HorizontalAlignment.RIGHT);
		}

		// 02.년월 디렉토리 생성:D:\\HR_FILE\2019\09
		String path = StringUtil.dynamicDir();
		String excelFileNm = StringUtil.getUUID();
		String ext = ".xlsx";
		String fileFullPath = path + File.separator + excelFileNm + ext;
		LOG.debug("=========================");
		LOG.debug("fileFullPath=" + fileFullPath);
		LOG.debug("=========================");
		// File Write
		File file = new File(fileFullPath);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			// workbook -> FileOutputStream
			workbook.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				if (null != workbook) {
					workbook.close();
				}

				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileFullPath;
	}

	public String csvWriter(List<User> list) {
		String fileFullPath = "";
		String path = StringUtil.dynamicDir();
		String excelFileNm = StringUtil.getUUID();
		String ext = ".csv";
		fileFullPath = path + File.separator + excelFileNm + ext;
		LOG.debug("=========================");
		LOG.debug("fileFullPath=" + fileFullPath);
		LOG.debug("=========================");

		FileWriter writer = null;
		try {
			//
			writer = new FileWriter(new File(fileFullPath));
			StringBuilder sb = new StringBuilder();
			// Header ID,이름,비번,Level,....
			sb.append("아이디");
			sb.append(",이름");
			sb.append(",비번");
			sb.append(",레벨");
			sb.append(",로그인");
			sb.append(",추천");
			sb.append(",이메일");
			sb.append(",등록일");
			sb.append("\n");
			// Header생성
			writer.write(sb.toString());

			// Data
			User user;
			String comma = ",";
			String lineSkip = "\n";
			for (int i = 0; i < list.size(); i++) {
				StringBuilder sbData = new StringBuilder();
				user = new User();
				user = list.get(i);
				sbData.append(user.getU_id());
				sbData.append(comma);
				sbData.append(user.getName());
				sbData.append(comma);
				sbData.append(user.getPasswd());
				sbData.append(comma);
				sbData.append(user.gethLevel().toString());
				sbData.append(comma);
				sbData.append(user.getLogin());
				sbData.append(comma);
				sbData.append(user.getRecommend());
				sbData.append(comma);
				sbData.append(user.getEmail());
				sbData.append(comma);
				sbData.append(user.getRegDt());
				// 마지막 라인 스킵 제거 !
				if (i != (list.size() - 1)) {
					sbData.append(lineSkip);
				}
				writer.write(sbData.toString());

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return fileFullPath;
	}

	public String xlsxWriter(List<User> list) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		// 워크Sheet생성
		XSSFSheet sheet = workbook.createSheet();
		// 행생성
		XSSFRow row = sheet.createRow(0);
		// Cell생성
		XSSFCell cell;

		// 헤더정보 생성
		cell = row.createCell(0);
		cell.setCellValue("ID");

		cell = row.createCell(1);
		cell.setCellValue("이름");

		cell = row.createCell(2);
		cell.setCellValue("비번");

		cell = row.createCell(3);
		cell.setCellValue("Level");

		cell = row.createCell(4);
		cell.setCellValue("로그인");

		cell = row.createCell(5);
		cell.setCellValue("추천");

		cell = row.createCell(6);
		cell.setCellValue("이메일");

		cell = row.createCell(7);
		cell.setCellValue("등록일");

		// 데이터 생성
		User user;
		for (int i = 0; i < list.size(); i++) {
			user = new User();
			user = list.get(i);

			row = sheet.createRow(i + 1);

			cell = row.createCell(0);
			cell.setCellValue(user.getU_id());

			cell = row.createCell(1);
			cell.setCellValue(user.getName());

			cell = row.createCell(2);
			cell.setCellValue(user.getPasswd());

			cell = row.createCell(3);
			cell.setCellValue(user.gethLevel().toString());

			cell = row.createCell(4);
			cell.setCellValue(user.getLogin());

			cell = row.createCell(5);
			cell.setCellValue(user.getRecommend());

			cell = row.createCell(6);
			cell.setCellValue(user.getEmail());

			cell = row.createCell(7);
			cell.setCellValue(user.getRegDt());
		}

		// 02.년월 디렉토리 생성:D:\\HR_FILE\2019\09
		String path = StringUtil.dynamicDir();
		String excelFileNm = StringUtil.getUUID();
		String ext = ".xlsx";
		String fileFullPath = path + File.separator + excelFileNm + ext;
		LOG.debug("=========================");
		LOG.debug("fileFullPath=" + fileFullPath);
		LOG.debug("=========================");
		// File Write
		File file = new File(fileFullPath);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			// workbook -> FileOutputStream
			workbook.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				if (null != workbook) {
					workbook.close();
				}

				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileFullPath;
	}

	/**
	 * getUUID:파일명생성 path: HR_FILE\2019\09\
	 * 
	 * @param list
	 * @return 파일 fullpath:D:\\HR_FILE\2019\09\중복되지 않는 파일명.xls
	 */
	public String xlsWriter(List<User> list) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		// 워크Sheet생성
		HSSFSheet sheet = workbook.createSheet();
		// 행생성
		HSSFRow row = sheet.createRow(0);
		// Cell생성
		HSSFCell cell;

		// 헤더정보 생성
		cell = row.createCell(0);
		cell.setCellValue("ID");

		cell = row.createCell(1);
		cell.setCellValue("이름");

		cell = row.createCell(2);
		cell.setCellValue("비번");

		cell = row.createCell(3);
		cell.setCellValue("Level");

		cell = row.createCell(4);
		cell.setCellValue("로그인");

		cell = row.createCell(5);
		cell.setCellValue("추천");

		cell = row.createCell(6);
		cell.setCellValue("이메일");

		cell = row.createCell(7);
		cell.setCellValue("등록일");

		// 데이터 생성
		User user;
		for (int i = 0; i < list.size(); i++) {
			user = new User();
			user = list.get(i);

			row = sheet.createRow(i + 1);

			cell = row.createCell(0);
			cell.setCellValue(user.getU_id());

			cell = row.createCell(1);
			cell.setCellValue(user.getName());

			cell = row.createCell(2);
			cell.setCellValue(user.getPasswd());

			cell = row.createCell(3);
			cell.setCellValue(user.gethLevel().toString());

			cell = row.createCell(4);
			cell.setCellValue(user.getLogin());

			cell = row.createCell(5);
			cell.setCellValue(user.getRecommend());

			cell = row.createCell(6);
			cell.setCellValue(user.getEmail());

			cell = row.createCell(7);
			cell.setCellValue(user.getRegDt());
		}

		// 02.년월 디렉토리 생성:D:\\HR_FILE\2019\09
		String path = StringUtil.dynamicDir();
		String excelFileNm = StringUtil.getUUID();
		String ext = ".xls";
		String fileFullPath = path + File.separator + excelFileNm + ext;
		LOG.debug("=========================");
		LOG.debug("fileFullPath=" + fileFullPath);
		LOG.debug("=========================");
		// File Write
		File file = new File(fileFullPath);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			// workbook -> FileOutputStream
			workbook.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				if (null != workbook) {
					workbook.close();
				}

				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileFullPath;
	}

}
