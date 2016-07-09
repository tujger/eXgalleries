package ru.wtg.exgalleries.helpers;

public class Item extends Entity<Item> {

	private static final long serialVersionUID = -5978985117168250995L;


	public Item() {
		super();
	}

	@Override
	protected Item getThis() {
		return this;
	}

/*	public Item setImageLink(String imageLink) {
		super.setImageLink(imageLink);
//		if (imageLink.length() > 0)
//			setName(md5Hash(imageLink));
		return getThis();
	}
*/
/*	private static String md5Hash(String st) {
		MessageDigest messageDigest = null;
		byte[] digest = new byte[0];

		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(st.getBytes());
			digest = messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			// тут можно обработать ошибку
			// возникает она если в передаваемый алгоритм в getInstance(,,,) не
			// существует
			e.printStackTrace();
		}

		BigInteger bigInt = new BigInteger(1, digest);
		String md5Hex = bigInt.toString(16);

		while (md5Hex.length() < 32) {
			md5Hex = "0" + md5Hex;
		}

		return md5Hex;
	}
*/
	public String toString() {
		return toString("");
	}


}
