package save;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PoetyRead {
	private String file = "项目1-全唐诗.txt";
	MongoClient client = null;
	private String zhizuo = "钱建文制作";
	private String fenge = "--------------------------------------------------------------------------------";

	public void readPoetry() {
		int count = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			Poetry poetry = null;
			String line = null;
			StringBuilder verseBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				count++;
				if (count % 5000 == 0) {
					System.out.println(count);
				}
				line = line.trim();
				if (matchEnd(line)) {
					// 结束
					break;
				}
				if (line.equals(zhizuo)) {
					// 去掉下面的卷～
					reader.readLine();
					reader.readLine();
					count += 2;
					continue;
				}
				if (line.equals(fenge)) {
					continue;
				}
				if (line.equals("")) {
					// 空行不处理
					continue;
				}
				if (match(line)) {
					// 存储上一首诗
					if (poetry != null) {
						poetry.setVerse(verseBuilder.toString());
						savePoetry(poetry);
					}
					// 开始新的一首诗
					poetry = new Poetry();
					verseBuilder = new StringBuilder();
					String[] array = line.split(" ");
					if (array.length != 2) {
						System.out.println(line);
						return;
					}
					String number = array[0];
					String[] array2 = array[1].split("】");
					if (array2.length != 2 && array2.length != 1) {
						System.out.println(line);
						return;
					}
					String title = array2[0].substring(1);
					String poet = (array2.length == 2) ? array2[1] : "";// 如果没有诗人，那么置为空字符串；
					poetry.setNumber(number);
					poetry.setTitle(title);
					poetry.setPoet(poet);
				} else {
					// 诗句
					verseBuilder.append(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("共" + count);
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client.close();
		}
	}

	private boolean match(String line) {
		String regex = "^卷\\d+_\\d+\\s.*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean matchEnd(String line) {
		String regex = "^回目录$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将读出的数据写入到mongodb中
	 */
	private void savePoetry(Poetry poetry) {
		if (client == null) {
			client = new MongoClient("localhost", 27017);
		}
		String databaseName = "allusion";
		MongoDatabase database = client.getDatabase(databaseName);
		String collectionName = "poetry";
		// database.createCollection(collectionName);
		MongoCollection<Document> collection = database.getCollection(collectionName);

		String numberKey = "number";
		String titleKey = "title";
		String poetKey = "poet";
		String verseKey = "verse";
		Document doc = new Document().append(numberKey, poetry.getNumber()).append(titleKey, poetry.getTitle())
				.append(poetKey, poetry.getPoet()).append(verseKey, poetry.getVerse());
		collection.insertOne(doc);
	}

	public static void main(String[] args) {
		PoetyRead read = new PoetyRead();
		// read.testMatch();
		read.readPoetry();
	}

	private void testMatch() {
		String line = "    卷1_1 【帝京篇十首】李世民 ";
		line = line.trim();
		System.out.println(line);
		System.out.println(match(line));
	}

}
