package com.paymill.android.payment;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.paymill.android.samples.vouchermill.R;

/**
 * This class contains a reusable card parsing logic.
 * 
 */
public class CardTypeParser {

	private static final String REGEX_VISA = "^4[0-9]*";
	private static final String REGEX_MASTERCARD = "^5[1-5][0-9]*";
	private static final String REGEX_MAESTRO = "^(5018|5020|5038|5893|6304|6331|6759|676[1-3]|6799|0604)[0-9]*";
	private static final String REGEX_AMERICANEXPRESS = "^3[47][0-9]*";
	private static final String REGEX_JCB = "^35((28)|(29)|[3-8][0-9])[0-9]*";
	private static final String REGEX_DINERSCLUB = "^(36|38|30[0-5])[0-9]*";
	private static final String REGEX_DISCOVER = "^(65|64[4-9]|6011|622(1(2[6-9]|[3-9][0-9])|[2-8][0-9]{2}|9([01][0-9]|2[0-5])))[0-9]*";
	private static final String REGEX_UNIONPAY = "^62[0-9]*";
	private static final String REGEX_INSTAPAYMENT = "^63[7-9][0-9]*";
	private static final String REGEX_LASER = "^(6704|6706|6771|6709)[0-9]*";

	private static final String REGEX_VISA_PARTIAL = "^4";
	private static final String REGEX_MASTERCARD_PARTIAL = "^5(?![^1-5])";
	private static final String REGEX_MAESTRO_PARTIAL = "^5(?![^08])(0?(?![^123])(1?(?![^8])|2?(?![^0])|3?(?![^8]))|(?<![^5])8?(?![^9])(?<![^8])9?(?![^3]))|^6((?![^3])3?((?![^0])0?(?![^4])|(?![^3])(?<![^3])3?(?![^1]))|(?![^7])7((?![^5])5?(?![^9])|(?![^6])6?(?![^1-3])|(?![^9])(?<![^7])9(?![^9])))|^0(?![^6])6?(?![^0])0?(?![^4])";
	private static final String REGEX_AMERICANEXPRESS_PARTIAL = "^3(?![^47])";
	private static final String REGEX_JCB_PARTIAL = "^3(?![^5])5?((?![^2])2?(?![^89])|(?![^3-8])(?<![^5])[3-8]?)";
	private static final String REGEX_DINERSCLUB_PARTIAL = "^3((?![^68])|0(?![^0-5]))";
	private static final String REGEX_DISCOVER_PARTIAL = "^6((?![^5])|(?![^4])4(?![^4-9])|(?![^0])0?($|(?![^1])1(?![^1])))";
	private static final String REGEX_UNIONPAY_PARTIAL = "^6(?![^2])";
	private static final String REGEX_INSTAPAYMENT_PARTIAL = "^6(?![^3])3?(?![^7-9])";
	private static final String REGEX_LASER_PARTIAL = "^6(?![^7])7?(?![^07])(0?(?![^469])|(?<![^7])7?(?![^1]))";

	private static final List<Integer> SPACES_POSITIONS_DINERSCLUB = Arrays
			.asList(4, 10);
	private static final List<Integer> SPACES_POSITIONS_AMERICANEXPRESS = Arrays
			.asList(4, 10);
	private static final List<Integer> SPACES_POSITIONS_UNIONPAY = Arrays
			.asList(6);
	private static final List<Integer> SPACES_POSITIONS_DEFAULT = Arrays
			.asList(4, 8, 12, 16);

	private static final List<Integer> MARKERS_POSITIONS_DINERSCLUB = Arrays
			.asList(5, 12, 17);
	private static final List<Integer> MARKERS_POSITIONS_AMERICANEXPRESS = Arrays
			.asList(5, 12, 18);
	private static final List<Integer> MARKERS_POSITIONS_UNIONPAY = Arrays
			.asList(7, 21);
	private static final List<Integer> MARKERS_POSITIONS_DEFAULT = Arrays
			.asList(5, 10, 15, 20);

	public static final String TAG = "CardTypeParser";

	/**
	 * Each card type accepted by PAYMILL.
	 * 
	 */
	public static enum CardType {
		Visa(16, 16, 3, true, REGEX_VISA, REGEX_VISA_PARTIAL,
				R.drawable.pm_visa, 3, SPACES_POSITIONS_DEFAULT,
				MARKERS_POSITIONS_DEFAULT), MasterCard(16, 16, 3, true,
				REGEX_MASTERCARD, REGEX_MASTERCARD_PARTIAL,
				R.drawable.pm_mastercard, 3, SPACES_POSITIONS_DEFAULT,
				MARKERS_POSITIONS_DEFAULT), Maestro(12, 19, 3, true,
				REGEX_MAESTRO, REGEX_MAESTRO_PARTIAL, R.drawable.pm_maestro, 4,
				SPACES_POSITIONS_DEFAULT, MARKERS_POSITIONS_DEFAULT), AmericanExpress(
				15, 15, 4, true, REGEX_AMERICANEXPRESS,
				REGEX_AMERICANEXPRESS_PARTIAL, R.drawable.pm_american_express,
				2, SPACES_POSITIONS_AMERICANEXPRESS,
				MARKERS_POSITIONS_AMERICANEXPRESS), JCB(16, 16, 3, true,
				REGEX_JCB, REGEX_JCB_PARTIAL, R.drawable.pm_jcb, 3,
				SPACES_POSITIONS_DEFAULT, MARKERS_POSITIONS_DEFAULT), DinersClub(
				14, 14, 3, true, REGEX_DINERSCLUB, REGEX_DINERSCLUB_PARTIAL,
				R.drawable.pm_diners_club, 2, SPACES_POSITIONS_DINERSCLUB,
				MARKERS_POSITIONS_DINERSCLUB), Discover(16, 16, 3, true,
				REGEX_DISCOVER, REGEX_DISCOVER_PARTIAL, R.drawable.pm_discover,
				3, SPACES_POSITIONS_DEFAULT, MARKERS_POSITIONS_DEFAULT), UnionPay(
				16, 19, 3, false, REGEX_UNIONPAY, REGEX_UNIONPAY_PARTIAL,
				R.drawable.pm_unionpay, 1, SPACES_POSITIONS_UNIONPAY,
				MARKERS_POSITIONS_UNIONPAY), InstaPayment(16, 16, 3, true,
				REGEX_INSTAPAYMENT, REGEX_INSTAPAYMENT_PARTIAL,
				R.drawable.pm_instapayment, 3, SPACES_POSITIONS_DEFAULT,
				MARKERS_POSITIONS_DEFAULT), Laser(16, 19, 3, true, REGEX_LASER,
				REGEX_LASER_PARTIAL, R.drawable.pm_laser, 4,
				SPACES_POSITIONS_DEFAULT, MARKERS_POSITIONS_DEFAULT), YetUnknown(
				12, 19, 4, true, "", "", 0, 4, SPACES_POSITIONS_DEFAULT,
				MARKERS_POSITIONS_DEFAULT), Invalid(12, 19, 4, true, "", "", 0,
				4, SPACES_POSITIONS_DEFAULT, MARKERS_POSITIONS_DEFAULT);

		int minLength, maxLength;
		int CVCLength;
		boolean isLuhn;
		String regExPattern;
		String regExPartialPattern;
		int imageId;
		int numberOfIntervals;
		List<Integer> spacesPositions;
		List<Integer> markersPositions;
		Pattern cardPattern;
		Matcher cardMatcher;
		Pattern partialCardPattern;
		Matcher partialCardMatcher;

		CardType(int minLength, int maxLength, int CVCLength, boolean isLuhn,
				String regExPattern, String regExPartialPattern, int imageId,
				int numberOfIntervals, List<Integer> spacesPositions,
				List<Integer> markersPositions) {
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.CVCLength = CVCLength;
			this.isLuhn = isLuhn;
			this.regExPattern = regExPattern;
			this.regExPartialPattern = regExPartialPattern;
			this.imageId = imageId;
			this.numberOfIntervals = numberOfIntervals;
			this.spacesPositions = spacesPositions;
			this.markersPositions = markersPositions;

			cardPattern = Pattern.compile(regExPattern);
			cardMatcher = cardPattern.matcher("");
			partialCardPattern = Pattern.compile(regExPartialPattern);
			partialCardMatcher = partialCardPattern.matcher("");
		}

		public int getId() {
			return ordinal();
		}

		public int getMinLength() {
			return minLength;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public int getCVCLength() {
			return CVCLength;
		}

		public boolean isLuhn() {
			return isLuhn;
		}

		public String getRegExPattern() {
			return regExPattern;
		}

		public String getRegExPartialPattern() {
			return regExPartialPattern;
		}

		public int getImageId() {
			return imageId;
		}

		public int getNumberOfIntervals() {
			return numberOfIntervals;
		}

		public List<Integer> getSpacesPositions() {
			return spacesPositions;
		}

		public List<Integer> getMarkersPositions() {
			return markersPositions;
		}

		/**
		 * Based on (partial) text, detects the card type.
		 * 
		 * @param cardNumber
		 *            a partial or complete input.
		 * @param allowedCardTypes
		 *            a list of all allowed card types.
		 * @return the detected card type, {@link CardType#Invalid} if the
		 *         string is not valid or {@link CardType#YetUnknown} if several
		 *         card types are possible for the supplied string.
		 */
		public static CardType getCardType(String cardNumber,
				Collection<CardType> allowedCardTypes) {
			boolean isValid = false;

			for (CardType cardType : CardType.values()) {
				if (cardType == CardType.YetUnknown
						|| cardType == CardType.Invalid
						|| !allowedCardTypes.contains(cardType)) {
					continue;
				}

				cardType.cardMatcher.reset(cardNumber);

				if (cardType.cardMatcher.find()) {
					Log.d(TAG, cardType.name());
					return cardType;
				}

				for (CardType partialCard : CardType.values()) {
					if (allowedCardTypes.contains(partialCard)) {

						partialCard.partialCardMatcher.reset(cardNumber);
						if (partialCard.partialCardMatcher.find()) {
							isValid = true;
							break;
						}
					}
				}

			}
			Log.d(TAG, Boolean.toString(isValid));

			if (isValid) {
				return CardType.YetUnknown;
			} else {
				return CardType.Invalid;
			}
		}

		public static CardType getCardTypeById(int id) {
			for (CardType cardType : CardType.values()) {
				if (id == cardType.getId())
					return cardType;
			}
			return CardType.Invalid;
		}
	}

}
