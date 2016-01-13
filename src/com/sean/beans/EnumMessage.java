package com.sean.beans;

/**
 * ö����Ϣͨ����
 * 
 * @author Sean
 * 
 */
public class EnumMessage {

	public enum Status {
		warn, error, success
	}
	public enum Message {
		Log_0001(""), ;

		private String message;

		Message(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.message;
		}
	}

}
