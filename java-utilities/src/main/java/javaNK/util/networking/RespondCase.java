package javaNK.util.networking;

public interface RespondCase
{
	String getType();
	void respond(JSON msg) throws Exception;
}