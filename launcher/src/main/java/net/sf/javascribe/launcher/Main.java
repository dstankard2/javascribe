package net.sf.javascribe.launcher;

public class Main {

	public static void main(String args[]) {
		String homeDir = System.getenv("JAVASCRIBE_HOME");
		Bootstrap boot = new Bootstrap();
		boolean run = false;

		boot.setHome(homeDir);
		
		for(String arg : args) {
			if (!boot.addParameter(arg)) {
				break;
			}
			run = true;
		}
		if (run) {
			boot.start();
		} else if (args.length==0) {
			boot.printUsage();
		}
	}

}
