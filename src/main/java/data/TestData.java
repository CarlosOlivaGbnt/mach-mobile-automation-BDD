package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mach.core.model.CreditCard;
import com.mach.core.model.Instructions;
import com.mach.core.util.EnumData;
import org.assertj.core.groups.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.groups.Tuple.tuple;

public abstract class TestData {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestData.class);
	private static final List<String> validPin = new ArrayList<>();

	private TestData() {
		throw new IllegalStateException("Utility class");
	}
	
	public static <T> List<T> getObjects(EnumData enumData) {
		List<T> elements = null;
		Gson gson = new Gson();
		Type collectionType = TypeToken.getParameterized(ArrayList.class, enumData.getaClass()).getType();
		try {
			elements = gson.fromJson(readFile(TestData.class.getResourceAsStream(enumData.getFile())), collectionType);
		} catch (IOException e) {
			LOGGER.error("Failed load JSON File: ", e);
		}
		return elements;
	}

	public static CreditCard getCreditCard(int index) {
		List<CreditCard> cards = getObjects(EnumData.CREDITCARDS);
		return cards != null && !cards.isEmpty() ? cards.get(index) : null;
	}

	public static List<Instructions> getInstructionsByType(String type) {
		List<Instructions> instructions = getObjects(EnumData.INSTRUCTIONS);
		return instructions == null ? null : instructions.stream().filter(instruction -> instruction.getType().equals(type))
				.collect(Collectors.toList());
	}

	public static List<String> getValidPIN() {
		if(validPin.isEmpty()){
			createNewPIN();
		}
		return new ArrayList<>(validPin);
	}

	public static List<String> createNewPIN() {
		validPin.clear();
		validPin.addAll(getRandomNumbersAsStringList(Collections.singletonList("0")));
		return new ArrayList<>(validPin);
	}

	//TODO: allow all rules from the US
	private static List<String> getRandomNumbersAsStringList(final List<String> oldPin) {
		List<String> randomNum;
		do {
			randomNum = getRandomNum();
		} while (oldPin.equals(randomNum));
		return randomNum;
	}

	private static List<String> getRandomNum() {
		return Arrays.asList(getMore("").split(""));
	}

	private static String getMore(String input) {
		int length = input.length();
		if (length >= 4) {
			return input;
		}
		List<Integer> digits = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		List<Integer> other = digits.stream()
				.filter(s -> {
					if(input.length() < 2) return true;
					Integer last = Integer.parseInt(input.substring(length - 1));
					Integer secondToLast = Integer.parseInt(input.substring(length - 2, length - 1));
					if(last.equals(secondToLast)) return !s.equals(last);
					if(last.equals(secondToLast + 1)) return s != last + 1;
					if(last.equals(secondToLast - 1)) return s != last - 1;
					return true;
				})
				.collect(Collectors.toList());
		Collections.shuffle(other);
		String longer = input + other.get(0).toString();
		return getMore(longer);
	}

	public static List<String> getValidSMSCode() {
		return asList("1", "2", "3", "4");
	}

	public static List<String> getInvalidPIN() {
		return asList("5", "6", "7", "8");
	}

	public static List<String> getPINAsList(String pin) {
		if (pin == null){
			LOGGER.error("pin is null", new Exception("ERROR: attribute -pin- do not exists in the -user- collection"));
			return new ArrayList<>();
		} else {
			return Arrays.asList(pin.split(""));			
		}
	}

	public static String getPINAsString(List<String> pin) {
		return pin.stream().map(Object::toString).collect(Collectors.joining());
	}

	public static Tuple getTuple(Instructions instructions) {
		return tuple(instructions.getTitle(), instructions.getDescription());
	}

	private static String readFile(InputStream inputStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}
		}
		return builder.toString();
	}
	
}