package ch.kumpel.builder;


public abstract class AbstractBuilder {
    private boolean verbose = true;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    protected void printf(String pattern, Object... args) {
        if (verbose) {
            System.out.printf(pattern, args);
        }
    }

}
