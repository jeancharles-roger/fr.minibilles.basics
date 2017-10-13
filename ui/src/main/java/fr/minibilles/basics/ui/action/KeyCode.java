package fr.minibilles.basics.ui.action;

import fr.minibilles.basics.Basics;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class KeyCode {

	private static final Map<Integer, String> keyDescription = new HashMap<Integer, String>();
	
	public static final String getKeyDescription(int key) {
		String description = keyDescription.get(key);
		if ( description == null ) {
			char[] chars = null;
			try {
				chars = Character.toChars(key);
			} catch (Throwable e) {
				chars = new char[] { '?', '?', '?' };
			}
			
			description ="'" + String.valueOf(chars) + "'";
		}
		return description;
	}
	/**
	 * ASCII character convenience constant for the backspace character
	 * (value is the <code>char</code> '\b').
	 */
	public static final char BS = '\b';

	/**
	 * ASCII character convenience constant for the carriage return character
	 * (value is the <code>char</code> '\r').
	 */
	public static final char CR = '\r';

	/**
	 * ASCII character convenience constant for the delete character
	 * (value is the <code>char</code> with value 127).
	 */
	public static final char DEL = 0x7F;
 
	/**
	 * ASCII character convenience constant for the escape character
	 * (value is the <code>char</code> with value 27).
	 */
	public static final char ESC = 0x1B;

	/**
	 * ASCII character convenience constant for the tab character
	 * (value is the <code>char</code> '\t').
	 * 
	 * @since 2.1
	 */
	public static final char TAB = '\t';
						
	/**
	 * ASCII character convenience constant for the space character
	 * (value is the <code>char</code> ' ').
	 * 
	 * @since 3.7
	 */
	public static final char SPACE = ' ';
						
	/**
	 * keyboard and/or mouse event mask indicating that the ALT key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;16).
	 */
	public static final int ALT = 1 << 16;
					
	/**
	 * Keyboard and/or mouse event mask indicating that the SHIFT key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;17).
	 */
	public static final int SHIFT = 1 << 17;
					
	/**
	 * Keyboard and/or mouse event mask indicating that the CTRL key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;18).
	 */
	public static final int CTRL = 1 << 18;

	/**
	 * Keyboard and/or mouse event mask indicating that the CTRL key
	 * was pushed on the keyboard when the event was generated. This
	 * is a synonym for CTRL (value is 1&lt;&lt;18).
	 */
	public static final int CONTROL = CTRL;

	/**
	 * Keyboard and/or mouse event mask indicating that the COMMAND key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;22).
	 * 
	 * @since 2.1
	 */
	public static final int COMMAND = 1 << 22;
	
	/**
	 * Keyboard and/or mouse event mask indicating that the MOD1 key
	 * was pushed on the keyboard when the event was generated.
	 * 
	 * This is the primary keyboard modifier for the platform.
	 * 
	 * @since 2.1
	 */
	public static final int MOD1;
	
	/**
	 * Keyboard and/or mouse event mask indicating that the MOD2 key
	 * was pushed on the keyboard when the event was generated.
	 * 
	 * This is the secondary keyboard modifier for the platform.
	 * 
	 * @since 2.1
	 */
	public static final int MOD2;

	/**
	 * Keyboard and/or mouse event mask indicating that the MOD3 key
	 * was pushed on the keyboard when the event was generated.
	 * 
	 * @since 2.1
	 */
	public static final int MOD3;

	/**
	 * Keyboard and/or mouse event mask indicating that the MOD4 key
	 * was pushed on the keyboard when the event was generated.
	 * 
	 * @since 2.1
	 */
	public static final int MOD4;
	
	/**
	 * Accelerator constant used to differentiate a key code from a
	 * unicode character.
	 * 
	 * If this bit is set, then the key stroke
	 * portion of an accelerator represents a key code.  If this bit
	 * is not set, then the key stroke portion of an accelerator is
	 * a unicode character.
	 * 
	 * The following expression is false:
	 * 
	 * <code>((SWT.MOD1 | SWT.MOD2 | 'T') & SWT.KEYCODE_BIT) != 0</code>.
	 * 
	 * The following expression is true:
	 * 
	 * <code>((SWT.MOD3 | SWT.F2) & SWT.KEYCODE_BIT) != 0</code>.
	 * 
	 * (value is (1&lt;&lt;24))
	 * 
	 * @since 2.1
	 */	
	public static final int KEYCODE_BIT = (1 << 24);

	/**
	 * Accelerator constant used to extract the key stroke portion of
	 * an accelerator.
	 * 
	 * The key stroke may be a key code or a unicode
	 * value.  If the key stroke is a key code <code>KEYCODE_BIT</code>
	 * will be set.
	 * 
	 * @since 2.1
	 */	
	public static final int KEY_MASK = KEYCODE_BIT + 0xFFFF;
	
	/**
	 * Keyboard event constant representing the UP ARROW key
	 * (value is (1&lt;&lt;24)+1).
	 */
	public static final int ARROW_UP = KEYCODE_BIT + 1;

	/**
	 * Keyboard event constant representing the DOWN ARROW key
	 * (value is (1&lt;&lt;24)+2).
	 */
	public static final int ARROW_DOWN = KEYCODE_BIT + 2;

	/**
	 * Keyboard event constant representing the LEFT ARROW key
	 * (value is (1&lt;&lt;24)+3).
	 */
	public static final int ARROW_LEFT = KEYCODE_BIT + 3;

	/**
	 * Keyboard event constant representing the RIGHT ARROW key
	 * (value is (1&lt;&lt;24)+4).
	 */
	public static final int ARROW_RIGHT = KEYCODE_BIT + 4;

	/**
	 * Keyboard event constant representing the PAGE UP key
	 * (value is (1&lt;&lt;24)+5).
	 */
	public static final int PAGE_UP = KEYCODE_BIT + 5;

	/**
	 * Keyboard event constant representing the PAGE DOWN key
	 * (value is (1&lt;&lt;24)+6).
	 */
	public static final int PAGE_DOWN = KEYCODE_BIT + 6;

	/**
	 * Keyboard event constant representing the HOME key
	 * (value is (1&lt;&lt;24)+7).
	 */
	public static final int HOME = KEYCODE_BIT + 7;

	/**
	 * Keyboard event constant representing the END key
	 * (value is (1&lt;&lt;24)+8).
	 */
	public static final int END = KEYCODE_BIT + 8;

	/**
	 * Keyboard event constant representing the INSERT key
	 * (value is (1&lt;&lt;24)+9).
	 */
	public static final int INSERT = KEYCODE_BIT + 9;

	/**
	 * Keyboard event constant representing the F1 key
	 * (value is (1&lt;&lt;24)+10).
	 */
	public static final int F1 = KEYCODE_BIT + 10;
	
	/**
	 * Keyboard event constant representing the F2 key
	 * (value is (1&lt;&lt;24)+11).
	 */
	public static final int F2 = KEYCODE_BIT + 11;
	
	/**
	 * Keyboard event constant representing the F3 key
	 * (value is (1&lt;&lt;24)+12).
	 */
	public static final int F3 = KEYCODE_BIT + 12;
	
	/**
	 * Keyboard event constant representing the F4 key
	 * (value is (1&lt;&lt;24)+13).
	 */
	public static final int F4 = KEYCODE_BIT + 13;
	
	/**
	 * Keyboard event constant representing the F5 key
	 * (value is (1&lt;&lt;24)+14).
	 */
	public static final int F5 = KEYCODE_BIT + 14;
	
	/**
	 * Keyboard event constant representing the F6 key
	 * (value is (1&lt;&lt;24)+15).
	 */
	public static final int F6 = KEYCODE_BIT + 15;
	
	/**
	 * Keyboard event constant representing the F7 key
	 * (value is (1&lt;&lt;24)+16).
	 */
	public static final int F7 = KEYCODE_BIT + 16;
	
	/**
	 * Keyboard event constant representing the F8 key
	 * (value is (1&lt;&lt;24)+17).
	 */
	public static final int F8 = KEYCODE_BIT + 17;
	
	/**
	 * Keyboard event constant representing the F9 key
	 * (value is (1&lt;&lt;24)+18).
	 */
	public static final int F9 = KEYCODE_BIT + 18;
	
	/**
	 * Keyboard event constant representing the F10 key
	 * (value is (1&lt;&lt;24)+19).
	 */
	public static final int F10 = KEYCODE_BIT + 19;
	
	/**
	 * Keyboard event constant representing the F11 key
	 * (value is (1&lt;&lt;24)+20).
	 */
	public static final int F11 = KEYCODE_BIT + 20;
	
	/**
	 * Keyboard event constant representing the F12 key
	 * (value is (1&lt;&lt;24)+21).
	 */
	public static final int F12 = KEYCODE_BIT + 21;

	/**
	 * Keyboard event constant representing the F13 key
	 * (value is (1&lt;&lt;24)+22).
	 * 
	 * @since 3.0
	 */
	public static final int F13 = KEYCODE_BIT + 22;
	
	/**
	 * Keyboard event constant representing the F14 key
	 * (value is (1&lt;&lt;24)+23).
	 * 
	 * @since 3.0
	 */
	public static final int F14 = KEYCODE_BIT + 23;
	
	/**
	 * Keyboard event constant representing the F15 key
	 * (value is (1&lt;&lt;24)+24).
	 * 
	 * @since 3.0
	 */
	public static final int F15 = KEYCODE_BIT + 24;
	
	/**
	 * Keyboard event constant representing the F16 key
	 * (value is (1&lt;&lt;25)+25).
	 * 
	 * @since 3.6
	 */
	public static final int F16 = KEYCODE_BIT + 25;

	
	/**
	 * Keyboard event constant representing the F17 key
	 * (value is (1&lt;&lt;26)+26).
	 * 
	 * @since 3.6
	 */
	public static final int F17 = KEYCODE_BIT + 26;

	
	/**
	 * Keyboard event constant representing the F18 key
	 * (value is (1&lt;&lt;27)+27).
	 * 
	 * @since 3.6
	 */
	public static final int F18 = KEYCODE_BIT + 27;

	
	/**
	 * Keyboard event constant representing the F19 key
	 * (value is (1&lt;&lt;28)+28).
	 * 
	 * @since 3.6
	 */
	public static final int F19 = KEYCODE_BIT + 28;
	
	/**
	 * Keyboard event constant representing the F20 key
	 * (value is (1&lt;&lt;29)+29).
	 * 
	 * @since 3.6
	 */
	public static final int F20 = KEYCODE_BIT + 29;
		
	/**
	 * Keyboard event constant representing the help
	 * key (value is (1&lt;&lt;24)+81).
	 * 
	 * NOTE: The HELP key maps to the key labeled "help",
	 * not "F1". If your keyboard does not have a HELP key,
	 * you will never see this key press.  To listen for
	 * help on a control, use SWT.Help.
	 * 
	 * @since 3.0
	 * 
	 * @see SWT#Help
	 */
	public static final int HELP = KEYCODE_BIT + 81;
	
	/**
	 * Keyboard event constant representing the pause
	 * key (value is (1&lt;&lt;24)+85).
	 * 
	 * @since 3.0
	 */
	public static final int PAUSE = KEYCODE_BIT + 85;
	
	/**
	 * Keyboard event constant representing the break
	 * key (value is (1&lt;&lt;24)+86).
	 * 
	 * @since 3.0
	 */
	public static final int BREAK = KEYCODE_BIT + 86;
	
	/**
	 * Keyboard event constant representing the print screen
	 * key (value is (1&lt;&lt;24)+87).
	 * 
	 * @since 3.0
	 */
	public static final int PRINT_SCREEN = KEYCODE_BIT + 87;

	/**
	 * Keyboard and/or mouse event mask indicating all possible
	 * keyboard modifiers.
	 * 
	 * To allow for the future, this mask  is intended to be used in 
	 * place of code that references  each individual keyboard mask. 
	 *  For example, the following expression will determine whether 
	 * any modifier is pressed and will continue to work as new modifier 
	 * masks are added.
	 * 
 	 * <code>(stateMask & SWT.MODIFIER_MASK) != 0</code>.
	 * 
	 * @since 2.1
	 */
	public static final int MODIFIER_MASK;

	static {
		/*
		* These values represent bit masks that may need to
		* expand in the future.  Therefore they are not initialized
		* in the declaration to stop the compiler from inlining.
		*/
		MODIFIER_MASK = ALT | SHIFT | CTRL | COMMAND;
		
		/*
		* These values can be different on different platforms.
		* Therefore they are not initialized in the declaration
		* to stop the compiler from inlining.
		*/
		if (Basics.isMac()) { //$NON-NLS-1$ //$NON-NLS-2$
			MOD1 = COMMAND;
			MOD2 = SHIFT;
			MOD3 = ALT;
			MOD4 = CONTROL;
		} else {
			MOD1 = CONTROL;
			MOD2 = SHIFT;
			MOD3 = ALT;
			MOD4 = 0;
		}
		
		
		// complete map
		keyDescription.put((int) BS, "Backspace");
		keyDescription.put((int) CR, "Return");
		keyDescription.put((int) DEL, "Del");
		keyDescription.put((int) ESC, "Escape");
		keyDescription.put((int) TAB, "Tab");
		keyDescription.put((int) SPACE, "Space");
		keyDescription.put(ALT, "Alt");
		keyDescription.put(SHIFT, "Shift");
		keyDescription.put(CTRL, "Ctrl");
		keyDescription.put(COMMAND, "Command");
		keyDescription.put(ARROW_LEFT, "\u2190");
		keyDescription.put(ARROW_UP, "\u2191");
		keyDescription.put(ARROW_RIGHT, "\u2192");
		keyDescription.put(ARROW_DOWN, "\u2193");
		keyDescription.put(PAGE_UP, "\u21DE");
		keyDescription.put(PAGE_DOWN, "\u21DF");
		keyDescription.put(HOME, "Home");
		keyDescription.put(END, "End");
		keyDescription.put(INSERT, "Insert");
		keyDescription.put(F1, "F1");
		keyDescription.put(F2, "F2");
		keyDescription.put(F3, "F3");
		keyDescription.put(F4, "F4");
		keyDescription.put(F5, "F5");
		keyDescription.put(F6, "F6");
		keyDescription.put(F7, "F7");
		keyDescription.put(F8, "F8");
		keyDescription.put(F9, "F9");
		keyDescription.put(F10, "F10");
		keyDescription.put(F11, "F11");
		keyDescription.put(F12, "F12");
		keyDescription.put(F13, "F13");
		keyDescription.put(F14, "F14");
		keyDescription.put(F15, "F15");
		keyDescription.put(F16, "F16");
		keyDescription.put(F17, "F17");
		keyDescription.put(F18, "F18");
		keyDescription.put(F19, "F19");
		keyDescription.put(F20, "F20");
		
		keyDescription.put(HELP, "Help");
		keyDescription.put(PAUSE, "Pause");
		keyDescription.put(BREAK, "Break");
		keyDescription.put(PRINT_SCREEN, "Print Screen");
		
	}

	private final int keycode;
	private final String description;
	
	public KeyCode(int ... keys) {
		if ( keys.length == 0 ) throw new IllegalArgumentException("Can't create key code with no key.");
		int keycode = 0;
		boolean containsCharacter = false;
		StringBuilder description = new StringBuilder();
		for ( int key : keys ) {
			if ( ( key & KEY_MASK ) != 0 ) {
				if ( containsCharacter ) {
					throw new IllegalArgumentException("Can't create key code with more than one letter.");
				} else {
					containsCharacter = true;
				}
			}
			keycode |= key;
			if ( description.length() > 0 ) description.append("+");
			description.append(getKeyDescription(key));
		}
		this.keycode = keycode;
		this.description = description.toString();
	}
	
	public int getKeycode() {
		return keycode;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean contains(char character) {
		char thisCharacter = (char) (keycode & KEY_MASK);
		return thisCharacter == character;
	}
	
	public boolean contains(int code) {
		return (keycode & code) != 0;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public static KeyCode fromEvent(KeyEvent event) {
		int[] keys = null;
		if ( event.stateMask == 0 || event.stateMask == event.keyCode ) {
			keys = new int[] { event.keyCode };
		} else {
			keys = new int[] { event.stateMask, event.keyCode };
		}
		return new KeyCode(keys);
	}
}
