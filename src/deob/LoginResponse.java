package deob;

public final class LoginResponse {

	public final String s1;
	public final String s2;
	public final boolean fatal;

	public LoginResponse(boolean fatal, String s1, String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
		this.fatal = fatal;
	}

	@Override
	public String toString()
	{
		return String.format("LoginResponse [s1=%s, s2=%s, fatal=%s]", s1, s2,
			fatal);
	}
}
