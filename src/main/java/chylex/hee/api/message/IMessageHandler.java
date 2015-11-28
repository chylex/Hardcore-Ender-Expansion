package chylex.hee.api.message;

@FunctionalInterface
public interface IMessageHandler{
	void call(MessageRunner runner);
}
