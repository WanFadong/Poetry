package save;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PoetryWrite {
	private BufferedWriter writer = null;
	private String file = "tangshi-15.txt";
	private String verseFile = "shiju.txt";

	private void writePoetry() {
		MongoClient client = new MongoClient("localhost", 27017);

		String databaseName = "allusion";
		MongoDatabase database = client.getDatabase(databaseName);
		String collectionName = "poetry";
		// database.createCollection(collectionName);
		MongoCollection<Document> collection = database.getCollection(collectionName);

		String numberKey = "number";
		String titleKey = "title";
		String poetKey = "poet";
		String verseKey = "verse";
		FindIterable<Document> iterator = collection.find();
		int count = 0;
		for (Document doc : iterator) {
			count++;
			if (count % 5000 == 0) {
				System.out.println(count);
			}
			String number = (String) doc.get(numberKey);
			String title = (String) doc.get(titleKey);
			String poet = (String) doc.get(poetKey);
			String verse = (String) doc.get(verseKey);
			writeToFile(number, title, poet, verse);
		}
		System.out.println("共" + count);
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client.close();
	}

	private void writeToFile(String number, String title, String poet, String verse) {
		try {
			if (writer == null) {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			}
			writer.write(number);
			writer.newLine();
			writer.write(title);
			writer.newLine();
			writer.write(poet);
			writer.newLine();
			writer.write(verse);
			writer.newLine();
			writer.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeVerse() {
		MongoClient client = new MongoClient("localhost", 27017);

		String databaseName = "allusion";
		MongoDatabase database = client.getDatabase(databaseName);
		String collectionName = "poetry";
		// database.createCollection(collectionName);
		MongoCollection<Document> collection = database.getCollection(collectionName);

		String verseKey = "verse";
		FindIterable<Document> iterator = collection.find();
		int count = 0;
		for (Document doc : iterator) {
			count++;
			if (count % 5000 == 0) {
				System.out.println(count);
			}
			String verse = (String) doc.get(verseKey);
			writeVerseToFile(verse);
		}
		System.out.println("共" + count);
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client.close();
	}

	private void writeVerseToFile(String verse) {
		try {
			if (writer == null) {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(verseFile)));
			}
			writer.write(verse);
			writer.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PoetryWrite write = new PoetryWrite();
		// write.writePoetry();
		write.writeVerse();
	}
}
