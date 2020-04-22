package pl.extollite.hidenseek.game;

import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.Language;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

/**
 * Game status types
 */
public enum Status {

	/**
	 * Game is running
	 */
	RUNNING,
	/**
	 * Game has stopped
	 */
	STOPPED,
	/**
	 * Game is ready to run
	 */
	READY,
	/**
	 * Game is waiting
	 */
	WAITING,
	/**
	 * Game is broken
	 */
	BROKEN,
	/**
	 * Game is not ready
	 */
	NOTREADY,
	/**
	 * Game is starting to run
	 */
	BEGINNING,
	/**
	 * Game is counting down to start
	 */
	COUNTDOWN;

	Language lang = HNS.getInstance().getLanguage();

	public String getName() {
        switch (this) {
            case RUNNING:
                return HNSUtils.colorize(lang.getStatus_running());
            case STOPPED:
                return HNSUtils.colorize(lang.getStatus_stopped());
            case READY:
                return HNSUtils.colorize(lang.getStatus_ready());
            case WAITING:
                return HNSUtils.colorize(lang.getStatus_waiting());
            case BROKEN:
                return HNSUtils.colorize(lang.getStatus_broken());
            case NOTREADY:
                return HNSUtils.colorize(lang.getStatus_not_ready());
            case BEGINNING:
                return HNSUtils.colorize(lang.getStatus_beginning());
            case COUNTDOWN:
                return HNSUtils.colorize(lang.getStatus_countdown());
            default:
                return HNSUtils.colorize("&cERROR!");
        }
	}

}
