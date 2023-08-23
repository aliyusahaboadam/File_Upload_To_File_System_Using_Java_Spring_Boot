package FileUploadApplication.exception;

public class ImageFileException  extends Exception {

	
	public ImageFileException (String message) {
		super(message);
	}
	
	public ImageFileException () {
		super("Failed Performing File Operation");
	}
}
