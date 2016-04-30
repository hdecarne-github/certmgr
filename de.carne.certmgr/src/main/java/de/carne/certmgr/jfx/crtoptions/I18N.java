/*
 * I18N resource strings
 *
 * Generated on 4/30/16 6:36 AM
 */
package de.carne.certmgr.jfx.crtoptions;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
final class I18N {

	static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N.class.getName());

	static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_NO_SUBJECT_DN_MESSAGE}
	 * <p>
	 * Please enter as subject for the certificate.
	 * </p>
	 */
	static final String STR_NO_SUBJECT_DN_MESSAGE = "STR_NO_SUBJECT_DN_MESSAGE";

	/**
	 * Resource string {@code STR_NO_SUBJECT_DN_MESSAGE}
	 * <p>
	 * Please enter as subject for the certificate.
	 * </p>
	 */
	static String formatSTR_NO_SUBJECT_DN_MESSAGE(Object... arguments) {
		return format(STR_NO_SUBJECT_DN_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_ALIAS_MESSAGE}
	 * <p>
	 * Please enter a name for the certificate.
	 * </p>
	 */
	static final String STR_NO_ALIAS_MESSAGE = "STR_NO_ALIAS_MESSAGE";

	/**
	 * Resource string {@code STR_NO_ALIAS_MESSAGE}
	 * <p>
	 * Please enter a name for the certificate.
	 * </p>
	 */
	static String formatSTR_NO_ALIAS_MESSAGE(Object... arguments) {
		return format(STR_NO_ALIAS_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_SAN_MESSAGE}
	 * <p>
	 * Please enter at least one name for the Subject Alternative Name extension.
	 * </p>
	 */
	static final String STR_NO_SAN_MESSAGE = "STR_NO_SAN_MESSAGE";

	/**
	 * Resource string {@code STR_NO_SAN_MESSAGE}
	 * <p>
	 * Please enter at least one name for the Subject Alternative Name extension.
	 * </p>
	 */
	static String formatSTR_NO_SAN_MESSAGE(Object... arguments) {
		return format(STR_NO_SAN_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CRT_OPTIONS_GENERATE_CSR_TITLE}
	 * <p>
	 * Generate Certificate Signing Request
	 * </p>
	 */
	static final String STR_CRT_OPTIONS_GENERATE_CSR_TITLE = "STR_CRT_OPTIONS_GENERATE_CSR_TITLE";

	/**
	 * Resource string {@code STR_CRT_OPTIONS_GENERATE_CSR_TITLE}
	 * <p>
	 * Generate Certificate Signing Request
	 * </p>
	 */
	static String formatSTR_CRT_OPTIONS_GENERATE_CSR_TITLE(Object... arguments) {
		return format(STR_CRT_OPTIONS_GENERATE_CSR_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_PATHLEN_CONSTRAINT_MESSAGE}
	 * <p>
	 * Please select a path length constraint for the Basic Constraints extension.
	 * </p>
	 */
	static final String STR_NO_PATHLEN_CONSTRAINT_MESSAGE = "STR_NO_PATHLEN_CONSTRAINT_MESSAGE";

	/**
	 * Resource string {@code STR_NO_PATHLEN_CONSTRAINT_MESSAGE}
	 * <p>
	 * Please select a path length constraint for the Basic Constraints extension.
	 * </p>
	 */
	static String formatSTR_NO_PATHLEN_CONSTRAINT_MESSAGE(Object... arguments) {
		return format(STR_NO_PATHLEN_CONSTRAINT_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_VALID_FROM_MESSAGE}
	 * <p>
	 * Please select a ''Valid from'' date.
	 * </p>
	 */
	static final String STR_NO_VALID_FROM_MESSAGE = "STR_NO_VALID_FROM_MESSAGE";

	/**
	 * Resource string {@code STR_NO_VALID_FROM_MESSAGE}
	 * <p>
	 * Please select a ''Valid from'' date.
	 * </p>
	 */
	static String formatSTR_NO_VALID_FROM_MESSAGE(Object... arguments) {
		return format(STR_NO_VALID_FROM_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CERT_ENTRY_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while accessing certificate entry ''{0}'' (Cause: ''{1}'')
	 * </p>
	 */
	static final String STR_CERT_ENTRY_ERROR_MESSAGE = "STR_CERT_ENTRY_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_CERT_ENTRY_ERROR_MESSAGE}
	 * <p>
	 * An error occurred while accessing certificate entry ''{0}'' (Cause: ''{1}'')
	 * </p>
	 */
	static String formatSTR_CERT_ENTRY_ERROR_MESSAGE(Object... arguments) {
		return format(STR_CERT_ENTRY_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_SELF_SIGNED_LABEL}
	 * <p>
	 * &lt;self-signed&gt;
	 * </p>
	 */
	static final String STR_SELF_SIGNED_LABEL = "STR_SELF_SIGNED_LABEL";

	/**
	 * Resource string {@code STR_SELF_SIGNED_LABEL}
	 * <p>
	 * &lt;self-signed&gt;
	 * </p>
	 */
	static String formatSTR_SELF_SIGNED_LABEL(Object... arguments) {
		return format(STR_SELF_SIGNED_LABEL, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_ISSUER_NAME_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid issuer name for the CRL Distribution Points extension.
	 * </p>
	 */
	static final String STR_INVALID_ISSUER_NAME_MESSAGE = "STR_INVALID_ISSUER_NAME_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_ISSUER_NAME_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid issuer name for the CRL Distribution Points extension.
	 * </p>
	 */
	static String formatSTR_INVALID_ISSUER_NAME_MESSAGE(Object... arguments) {
		return format(STR_INVALID_ISSUER_NAME_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CRT_OPTIONS_REGENERATE_CRT_TITLE}
	 * <p>
	 * Re-sign Certificate
	 * </p>
	 */
	static final String STR_CRT_OPTIONS_REGENERATE_CRT_TITLE = "STR_CRT_OPTIONS_REGENERATE_CRT_TITLE";

	/**
	 * Resource string {@code STR_CRT_OPTIONS_REGENERATE_CRT_TITLE}
	 * <p>
	 * Re-sign Certificate
	 * </p>
	 */
	static String formatSTR_CRT_OPTIONS_REGENERATE_CRT_TITLE(Object... arguments) {
		return format(STR_CRT_OPTIONS_REGENERATE_CRT_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_CRT_OPTIONS_REGENERATE_CSR_TITLE}
	 * <p>
	 * Re-sign Certificate Signing Request
	 * </p>
	 */
	static final String STR_CRT_OPTIONS_REGENERATE_CSR_TITLE = "STR_CRT_OPTIONS_REGENERATE_CSR_TITLE";

	/**
	 * Resource string {@code STR_CRT_OPTIONS_REGENERATE_CSR_TITLE}
	 * <p>
	 * Re-sign Certificate Signing Request
	 * </p>
	 */
	static String formatSTR_CRT_OPTIONS_REGENERATE_CSR_TITLE(Object... arguments) {
		return format(STR_CRT_OPTIONS_REGENERATE_CSR_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_KEY_ALG_MESSAGE}
	 * <p>
	 * Please select a key algorithm.
	 * </p>
	 */
	static final String STR_NO_KEY_ALG_MESSAGE = "STR_NO_KEY_ALG_MESSAGE";

	/**
	 * Resource string {@code STR_NO_KEY_ALG_MESSAGE}
	 * <p>
	 * Please select a key algorithm.
	 * </p>
	 */
	static String formatSTR_NO_KEY_ALG_MESSAGE(Object... arguments) {
		return format(STR_NO_KEY_ALG_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_ALIAS_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name.
	 * </p>
	 */
	static final String STR_INVALID_ALIAS_MESSAGE = "STR_INVALID_ALIAS_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_ALIAS_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name.
	 * </p>
	 */
	static String formatSTR_INVALID_ALIAS_MESSAGE(Object... arguments) {
		return format(STR_INVALID_ALIAS_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CRITICAL_EXT_LABEL}
	 * <p>
	 * Critical
	 * </p>
	 */
	static final String STR_CRITICAL_EXT_LABEL = "STR_CRITICAL_EXT_LABEL";

	/**
	 * Resource string {@code STR_CRITICAL_EXT_LABEL}
	 * <p>
	 * Critical
	 * </p>
	 */
	static String formatSTR_CRITICAL_EXT_LABEL(Object... arguments) {
		return format(STR_CRITICAL_EXT_LABEL, arguments);
	}

	/**
	 * Resource key {@code STR_NO_VALID_TO_MESSAGE}
	 * <p>
	 * Please select a ''Valid to'' date.
	 * </p>
	 */
	static final String STR_NO_VALID_TO_MESSAGE = "STR_NO_VALID_TO_MESSAGE";

	/**
	 * Resource string {@code STR_NO_VALID_TO_MESSAGE}
	 * <p>
	 * Please select a ''Valid to'' date.
	 * </p>
	 */
	static String formatSTR_NO_VALID_TO_MESSAGE(Object... arguments) {
		return format(STR_NO_VALID_TO_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_SUBJECT_DN_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid subject.
	 * </p>
	 */
	static final String STR_INVALID_SUBJECT_DN_MESSAGE = "STR_INVALID_SUBJECT_DN_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_SUBJECT_DN_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid subject.
	 * </p>
	 */
	static String formatSTR_INVALID_SUBJECT_DN_MESSAGE(Object... arguments) {
		return format(STR_INVALID_SUBJECT_DN_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a signature algorithm.
	 * </p>
	 */
	static final String STR_NO_SIG_ALG_MESSAGE = "STR_NO_SIG_ALG_MESSAGE";

	/**
	 * Resource string {@code STR_NO_SIG_ALG_MESSAGE}
	 * <p>
	 * Please select a signature algorithm.
	 * </p>
	 */
	static String formatSTR_NO_SIG_ALG_MESSAGE(Object... arguments) {
		return format(STR_NO_SIG_ALG_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NON_CRITICAL_EXT_LABEL}
	 * <p>
	 * Non-critical
	 * </p>
	 */
	static final String STR_NON_CRITICAL_EXT_LABEL = "STR_NON_CRITICAL_EXT_LABEL";

	/**
	 * Resource string {@code STR_NON_CRITICAL_EXT_LABEL}
	 * <p>
	 * Non-critical
	 * </p>
	 */
	static String formatSTR_NON_CRITICAL_EXT_LABEL(Object... arguments) {
		return format(STR_NON_CRITICAL_EXT_LABEL, arguments);
	}

	/**
	 * Resource key {@code STR_UNUSED_EXT_LABEL}
	 * <p>
	 * Not used
	 * </p>
	 */
	static final String STR_UNUSED_EXT_LABEL = "STR_UNUSED_EXT_LABEL";

	/**
	 * Resource string {@code STR_UNUSED_EXT_LABEL}
	 * <p>
	 * Not used
	 * </p>
	 */
	static String formatSTR_UNUSED_EXT_LABEL(Object... arguments) {
		return format(STR_UNUSED_EXT_LABEL, arguments);
	}

	/**
	 * Resource key {@code STR_NO_EXTENDED_KEY_USAGE_MESSAGE}
	 * <p>
	 * Please select at least one key usage for the Extended Key Usage extension.
	 * </p>
	 */
	static final String STR_NO_EXTENDED_KEY_USAGE_MESSAGE = "STR_NO_EXTENDED_KEY_USAGE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_EXTENDED_KEY_USAGE_MESSAGE}
	 * <p>
	 * Please select at least one key usage for the Extended Key Usage extension.
	 * </p>
	 */
	static String formatSTR_NO_EXTENDED_KEY_USAGE_MESSAGE(Object... arguments) {
		return format(STR_NO_EXTENDED_KEY_USAGE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_VALIDITY_MESSAGE}
	 * <p>
	 * The selected validity range is invalid or empty. Please select suitable ''Valid from'' and ''Valid to'' dates.
	 * </p>
	 */
	static final String STR_INVALID_VALIDITY_MESSAGE = "STR_INVALID_VALIDITY_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_VALIDITY_MESSAGE}
	 * <p>
	 * The selected validity range is invalid or empty. Please select suitable ''Valid from'' and ''Valid to'' dates.
	 * </p>
	 */
	static String formatSTR_INVALID_VALIDITY_MESSAGE(Object... arguments) {
		return format(STR_INVALID_VALIDITY_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_GENERATE_ERROR_MESSAGE}
	 * <p>
	 * Generation of Certificate&frasl;Certificate Signing Request failed. See details for further information.
	 * </p>
	 */
	static final String STR_GENERATE_ERROR_MESSAGE = "STR_GENERATE_ERROR_MESSAGE";

	/**
	 * Resource string {@code STR_GENERATE_ERROR_MESSAGE}
	 * <p>
	 * Generation of Certificate&frasl;Certificate Signing Request failed. See details for further information.
	 * </p>
	 */
	static String formatSTR_GENERATE_ERROR_MESSAGE(Object... arguments) {
		return format(STR_GENERATE_ERROR_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_KEY_SIZE_MESSAGE}
	 * <p>
	 * Please select a key size.
	 * </p>
	 */
	static final String STR_NO_KEY_SIZE_MESSAGE = "STR_NO_KEY_SIZE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_KEY_SIZE_MESSAGE}
	 * <p>
	 * Please select a key size.
	 * </p>
	 */
	static String formatSTR_NO_KEY_SIZE_MESSAGE(Object... arguments) {
		return format(STR_NO_KEY_SIZE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_SAN_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name for the Subject Alternative Name extension.
	 * </p>
	 */
	static final String STR_INVALID_SAN_MESSAGE = "STR_INVALID_SAN_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_SAN_MESSAGE}
	 * <p>
	 * ''{0}'' is not a valid name for the Subject Alternative Name extension.
	 * </p>
	 */
	static String formatSTR_INVALID_SAN_MESSAGE(Object... arguments) {
		return format(STR_INVALID_SAN_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_KEY_USAGE_MESSAGE}
	 * <p>
	 * Please select at least one key usage for the Key Usage extension.
	 * </p>
	 */
	static final String STR_NO_KEY_USAGE_MESSAGE = "STR_NO_KEY_USAGE_MESSAGE";

	/**
	 * Resource string {@code STR_NO_KEY_USAGE_MESSAGE}
	 * <p>
	 * Please select at least one key usage for the Key Usage extension.
	 * </p>
	 */
	static String formatSTR_NO_KEY_USAGE_MESSAGE(Object... arguments) {
		return format(STR_NO_KEY_USAGE_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_NO_ISSUER_NAME_MESSAGE}
	 * <p>
	 * Please enter at least one issuer name for the CRL Distribution Points extension.
	 * </p>
	 */
	static final String STR_NO_ISSUER_NAME_MESSAGE = "STR_NO_ISSUER_NAME_MESSAGE";

	/**
	 * Resource string {@code STR_NO_ISSUER_NAME_MESSAGE}
	 * <p>
	 * Please enter at least one issuer name for the CRL Distribution Points extension.
	 * </p>
	 */
	static String formatSTR_NO_ISSUER_NAME_MESSAGE(Object... arguments) {
		return format(STR_NO_ISSUER_NAME_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_CRT_OPTIONS_GENERATE_CRT_TITLE}
	 * <p>
	 * Generate Certificate
	 * </p>
	 */
	static final String STR_CRT_OPTIONS_GENERATE_CRT_TITLE = "STR_CRT_OPTIONS_GENERATE_CRT_TITLE";

	/**
	 * Resource string {@code STR_CRT_OPTIONS_GENERATE_CRT_TITLE}
	 * <p>
	 * Generate Certificate
	 * </p>
	 */
	static String formatSTR_CRT_OPTIONS_GENERATE_CRT_TITLE(Object... arguments) {
		return format(STR_CRT_OPTIONS_GENERATE_CRT_TITLE, arguments);
	}

}
