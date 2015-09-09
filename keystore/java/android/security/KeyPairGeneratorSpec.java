/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.security;

import android.content.Context;
import android.text.TextUtils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 * This provides the required parameters needed for initializing the
 * {@code KeyPairGenerator} that works with
 * <a href="{@docRoot}training/articles/keystore.html">Android KeyStore
 * facility</a>. The Android KeyStore facility is accessed through a
 * {@link java.security.KeyPairGenerator} API using the {@code AndroidKeyStore}
 * provider. The {@code context} passed in may be used to pop up some UI to ask
 * the user to unlock or initialize the Android KeyStore facility.
 * <p>
 * After generation, the {@code keyStoreAlias} is used with the
 * {@link java.security.KeyStore#getEntry(String, java.security.KeyStore.ProtectionParameter)}
 * interface to retrieve the {@link PrivateKey} and its associated
 * {@link Certificate} chain.
 * <p>
 * The KeyPair generator will create a self-signed certificate with the subject
 * as its X.509v3 Subject Distinguished Name and as its X.509v3 Issuer
 * Distinguished Name along with the other parameters specified with the
 * {@link Builder}.
 * <p>
 * The self-signed X.509 certificate may be replaced at a later time by a
 * certificate signed by a real Certificate Authority.
 */
public final class KeyPairGeneratorSpec implements AlgorithmParameterSpec {

    private static final X500Principal DEFAULT_CERT_SUBJECT = new X500Principal("CN=fake");
    private static final BigInteger DEFAULT_CERT_SERIAL_NUMBER = new BigInteger("1");
    private static final Date DEFAULT_CERT_NOT_BEFORE = new Date(0L); // Jan 1 1970
    private static final Date DEFAULT_CERT_NOT_AFTER = new Date(2461449600000L); // Jan 1 2048

    private final Context mContext;

    private final String mKeystoreAlias;

    private final String mKeyType;

    private final int mKeySize;

    private final AlgorithmParameterSpec mSpec;

    private final X500Principal mSubjectDN;

    private final BigInteger mSerialNumber;

    private final Date mStartDate;

    private final Date mEndDate;

    private final int mFlags;

    private final Date mKeyValidityStart;

    private final Date mKeyValidityForOriginationEnd;

    private final Date mKeyValidityForConsumptionEnd;

    private final @KeyStoreKeyProperties.PurposeEnum int mPurposes;

    private final String[] mDigests;

    private final String[] mEncryptionPaddings;

    private final String[] mSignaturePaddings;

    private final String[] mBlockModes;

    private final boolean mRandomizedEncryptionRequired;

    private final @KeyStoreKeyProperties.UserAuthenticatorEnum int mUserAuthenticators;

    private final int mUserAuthenticationValidityDurationSeconds;

    /**
     * Parameter specification for the "{@code AndroidKeyPairGenerator}"
     * instance of the {@link java.security.KeyPairGenerator} API. The
     * {@code context} passed in may be used to pop up some UI to ask the user
     * to unlock or initialize the Android keystore facility.
     * <p>
     * After generation, the {@code keyStoreAlias} is used with the
     * {@link java.security.KeyStore#getEntry(String, java.security.KeyStore.ProtectionParameter)}
     * interface to retrieve the {@link PrivateKey} and its associated
     * {@link Certificate} chain.
     * <p>
     * The KeyPair generator will create a self-signed certificate with the
     * properties of {@code subjectDN} as its X.509v3 Subject Distinguished Name
     * and as its X.509v3 Issuer Distinguished Name, using the specified
     * {@code serialNumber}, and the validity date starting at {@code startDate}
     * and ending at {@code endDate}.
     *
     * @param context Android context for the activity
     * @param keyStoreAlias name to use for the generated key in the Android
     *            keystore
     * @param keyType key algorithm to use (EC, RSA)
     * @param keySize size of key to generate
     * @param spec the underlying key type parameters
     * @param subjectDN X.509 v3 Subject Distinguished Name
     * @param serialNumber X509 v3 certificate serial number
     * @param startDate the start of the self-signed certificate validity period
     * @param endDate the end date of the self-signed certificate validity
     *            period
     * @throws IllegalArgumentException when any argument is {@code null} or
     *             {@code endDate} is before {@code startDate}.
     * @hide should be built with KeyPairGeneratorSpecBuilder
     */
    public KeyPairGeneratorSpec(Context context, String keyStoreAlias, String keyType, int keySize,
            AlgorithmParameterSpec spec, X500Principal subjectDN, BigInteger serialNumber,
            Date startDate, Date endDate, int flags,
            Date keyValidityStart,
            Date keyValidityForOriginationEnd,
            Date keyValidityForConsumptionEnd,
            @KeyStoreKeyProperties.PurposeEnum int purposes,
            String[] digests,
            String[] encryptionPaddings,
            String[] signaturePaddings,
            String[] blockModes,
            boolean randomizedEncryptionRequired,
            @KeyStoreKeyProperties.UserAuthenticatorEnum int userAuthenticators,
            int userAuthenticationValidityDurationSeconds) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        } else if (TextUtils.isEmpty(keyStoreAlias)) {
            throw new IllegalArgumentException("keyStoreAlias must not be empty");
        } else if ((userAuthenticationValidityDurationSeconds < 0)
                && (userAuthenticationValidityDurationSeconds != -1)) {
            throw new IllegalArgumentException(
                    "userAuthenticationValidityDurationSeconds must not be negative");
        }

        if (subjectDN == null) {
            subjectDN = DEFAULT_CERT_SUBJECT;
        }
        if (startDate == null) {
            startDate = DEFAULT_CERT_NOT_BEFORE;
        }
        if (endDate == null) {
            endDate = DEFAULT_CERT_NOT_AFTER;
        }
        if (serialNumber == null) {
            serialNumber = DEFAULT_CERT_SERIAL_NUMBER;
        }

        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("endDate < startDate");
        }

        mContext = context;
        mKeystoreAlias = keyStoreAlias;
        mKeyType = keyType;
        mKeySize = keySize;
        mSpec = spec;
        mSubjectDN = subjectDN;
        mSerialNumber = serialNumber;
        mStartDate = startDate;
        mEndDate = endDate;
        mFlags = flags;
        mKeyValidityStart = keyValidityStart;
        mKeyValidityForOriginationEnd = keyValidityForOriginationEnd;
        mKeyValidityForConsumptionEnd = keyValidityForConsumptionEnd;
        mPurposes = purposes;
        mDigests = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(digests));
        mEncryptionPaddings =
                ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(encryptionPaddings));
        mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(signaturePaddings));
        mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(blockModes));
        mRandomizedEncryptionRequired = randomizedEncryptionRequired;
        mUserAuthenticators = userAuthenticators;
        mUserAuthenticationValidityDurationSeconds = userAuthenticationValidityDurationSeconds;
    }

    /**
     * TODO: Remove this constructor once tests are switched over to the new one above.
     * @hide
     */
    public KeyPairGeneratorSpec(Context context, String keyStoreAlias, String keyType, int keySize,
            AlgorithmParameterSpec spec, X500Principal subjectDN, BigInteger serialNumber,
            Date startDate, Date endDate, int flags) {
        this(context,
                keyStoreAlias,
                keyType,
                keySize,
                spec,
                subjectDN,
                serialNumber,
                startDate,
                endDate,
                flags,
                startDate,
                endDate,
                endDate,
                0, // purposes
                null, // digests
                null, // encryption paddings
                null, // signature paddings
                null, // block modes
                false, // randomized encryption required
                0, // user authenticators
                -1 // user authentication validity duration (seconds)
                );
    }

    /**
     * Gets the Android context used for operations with this instance.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Returns the alias that will be used in the {@code java.security.KeyStore}
     * in conjunction with the {@code AndroidKeyStore}.
     */
    public String getKeystoreAlias() {
        return mKeystoreAlias;
    }

    /**
     * Returns the key type (e.g., "EC", "RSA") specified by this parameter.
     */
    public String getKeyType() {
        return mKeyType;
    }

    /**
     * Returns the key size specified by this parameter. For instance, for RSA
     * this will return the modulus size and for EC it will return the field
     * size.
     */
    public int getKeySize() {
        return mKeySize;
    }

    /**
     * Returns the {@link AlgorithmParameterSpec} that will be used for creation
     * of the key pair.
     */
    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return mSpec;
    }

    /**
     * Gets the subject distinguished name to be used on the X.509 certificate
     * that will be put in the {@link java.security.KeyStore}.
     */
    public X500Principal getSubjectDN() {
        return mSubjectDN;
    }

    /**
     * Gets the serial number to be used on the X.509 certificate that will be
     * put in the {@link java.security.KeyStore}.
     */
    public BigInteger getSerialNumber() {
        return mSerialNumber;
    }

    /**
     * Gets the start date to be used on the X.509 certificate that will be put
     * in the {@link java.security.KeyStore}.
     */
    public Date getStartDate() {
        return mStartDate;
    }

    /**
     * Gets the end date to be used on the X.509 certificate that will be put in
     * the {@link java.security.KeyStore}.
     */
    public Date getEndDate() {
        return mEndDate;
    }

    /**
     * @hide
     */
    int getFlags() {
        return mFlags;
    }

    /**
     * Returns {@code true} if this parameter will require generated keys to be
     * encrypted in the {@link java.security.KeyStore}.
     */
    public boolean isEncryptionRequired() {
        return (mFlags & KeyStore.FLAG_ENCRYPTED) != 0;
    }

    /**
     * Gets the time instant before which the key pair is not yet valid.
     *
     * @return instant or {@code null} if not restricted.
     *
     * @hide
     */
    public Date getKeyValidityStart() {
        return mKeyValidityStart;
    }

    /**
     * Gets the time instant after which the key pair is no longer valid for decryption and
     * verification.
     *
     * @return instant or {@code null} if not restricted.
     *
     * @hide
     */
    public Date getKeyValidityForConsumptionEnd() {
        return mKeyValidityForConsumptionEnd;
    }

    /**
     * Gets the time instant after which the key pair is no longer valid for encryption and signing.
     *
     * @return instant or {@code null} if not restricted.
     *
     * @hide
     */
    public Date getKeyValidityForOriginationEnd() {
        return mKeyValidityForOriginationEnd;
    }

    /**
     * Gets the set of purposes for which the key can be used.
     *
     * @hide
     */
    public @KeyStoreKeyProperties.PurposeEnum int getPurposes() {
        return mPurposes;
    }

    /**
     * Gets the set of digest algorithms with which the key can be used.
     *
     * @hide
     */
    public String[] getDigests() {
        return ArrayUtils.cloneIfNotEmpty(mDigests);
    }

    /**
     * Gets the set of padding schemes with which the key can be used when encrypting/decrypting.
     *
     * @hide
     */
    public String[] getEncryptionPaddings() {
        return ArrayUtils.cloneIfNotEmpty(mEncryptionPaddings);
    }

    /**
     * Gets the set of padding schemes with which the key can be used when signing/verifying.
     *
     * @hide
     */
    public String[] getSignaturePaddings() {
        return ArrayUtils.cloneIfNotEmpty(mSignaturePaddings);
    }

    /**
     * Gets the set of block modes with which the key can be used.
     *
     * @hide
     */
    public String[] getBlockModes() {
        return ArrayUtils.cloneIfNotEmpty(mBlockModes);
    }

    /**
     * Returns {@code true} if encryption using this key must be sufficiently randomized to produce
     * different ciphertexts for the same plaintext every time. The formal cryptographic property
     * being required is <em>indistinguishability under chosen-plaintext attack ({@code
     * IND-CPA})</em>. This property is important because it mitigates several classes of
     * weaknesses due to which ciphertext may leak information about plaintext.  For example, if a
     * given plaintext always produces the same ciphertext, an attacker may see the repeated
     * ciphertexts and be able to deduce something about the plaintext.
     *
     * @hide
     */
    public boolean isRandomizedEncryptionRequired() {
        return mRandomizedEncryptionRequired;
    }

    /**
     * Gets the set of user authenticators which protect access to the private key. The key can only
     * be used iff the user has authenticated to at least one of these user authenticators.
     *
     * <p>This restriction applies only to private key operations. Public key operations are not
     * restricted.
     *
     * @return user authenticators or {@code 0} if the key can be used without user authentication.
     *
     * @hide
     */
    public @KeyStoreKeyProperties.UserAuthenticatorEnum int getUserAuthenticators() {
        return mUserAuthenticators;
    }

    /**
     * Gets the duration of time (seconds) for which the private key can be used after the user
     * successfully authenticates to one of the associated user authenticators.
     *
     * <p>This restriction applies only to private key operations. Public key operations are not
     * restricted.
     *
     * @return duration in seconds or {@code -1} if not restricted. {@code 0} means authentication
     *         is required for every use of the key.
     *
     * @hide
     */
    public int getUserAuthenticationValidityDurationSeconds() {
        return mUserAuthenticationValidityDurationSeconds;
    }

    /**
     * Builder class for {@link KeyPairGeneratorSpec} objects.
     * <p>
     * This will build a parameter spec for use with the <a href="{@docRoot}
     * training/articles/keystore.html">Android KeyStore facility</a>.
     * <p>
     * The required fields must be filled in with the builder.
     * <p>
     * Example:
     *
     * <pre class="prettyprint">
     * Calendar start = new Calendar();
     * Calendar end = new Calendar();
     * end.add(1, Calendar.YEAR);
     *
     * KeyPairGeneratorSpec spec =
     *         new KeyPairGeneratorSpec.Builder(mContext).setAlias(&quot;myKey&quot;)
     *                 .setSubject(new X500Principal(&quot;CN=myKey&quot;)).setSerial(BigInteger.valueOf(1337))
     *                 .setStartDate(start.getTime()).setEndDate(end.getTime()).build();
     * </pre>
     */
    public final static class Builder {
        private final Context mContext;

        private String mKeystoreAlias;

        private String mKeyType;

        private int mKeySize = -1;

        private AlgorithmParameterSpec mSpec;

        private X500Principal mSubjectDN;

        private BigInteger mSerialNumber;

        private Date mStartDate;

        private Date mEndDate;

        private int mFlags;

        private Date mKeyValidityStart;

        private Date mKeyValidityForOriginationEnd;

        private Date mKeyValidityForConsumptionEnd;

        private @KeyStoreKeyProperties.PurposeEnum int mPurposes;

        private String[] mDigests;

        private String[] mEncryptionPaddings;

        private String[] mSignaturePaddings;

        private String[] mBlockModes;

        private boolean mRandomizedEncryptionRequired = true;

        private @KeyStoreKeyProperties.UserAuthenticatorEnum int mUserAuthenticators;

        private int mUserAuthenticationValidityDurationSeconds = -1;

        /**
         * Creates a new instance of the {@code Builder} with the given
         * {@code context}. The {@code context} passed in may be used to pop up
         * some UI to ask the user to unlock or initialize the Android KeyStore
         * facility.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
            mContext = context;
        }

        /**
         * Sets the alias to be used to retrieve the key later from a
         * {@link java.security.KeyStore} instance using the
         * {@code AndroidKeyStore} provider.
         */
        public Builder setAlias(String alias) {
            if (alias == null) {
                throw new NullPointerException("alias == null");
            }
            mKeystoreAlias = alias;
            return this;
        }

        /**
         * Sets the key type (e.g., EC, RSA) of the keypair to be created.
         */
        public Builder setKeyType(String keyType) throws NoSuchAlgorithmException {
            if (keyType == null) {
                throw new NullPointerException("keyType == null");
            } else {
                if (KeyStore.getKeyTypeForAlgorithm(keyType) == -1) {
                    throw new NoSuchAlgorithmException("Unsupported key type: " + keyType);
                }
            }
            mKeyType = keyType;
            return this;
        }

        /**
         * Sets the key size for the keypair to be created. For instance, for a
         * key type of RSA this will set the modulus size and for a key type of
         * EC it will select a curve with a matching field size.
         */
        public Builder setKeySize(int keySize) {
            if (keySize < 0) {
                throw new IllegalArgumentException("keySize < 0");
            }
            mKeySize = keySize;
            return this;
        }

        /**
         * Sets the algorithm-specific key generation parameters. For example, for RSA keys
         * this may be an instance of {@link java.security.spec.RSAKeyGenParameterSpec}.
         */
        public Builder setAlgorithmParameterSpec(AlgorithmParameterSpec spec) {
            if (spec == null) {
                throw new NullPointerException("spec == null");
            }
            mSpec = spec;
            return this;
        }

        /**
         * Sets the subject used for the self-signed certificate of the
         * generated key pair.
         *
         * <p>The subject must be specified on API Level
         * {@link android.os.Build.VERSION_CODES#LOLLIPOP_MR1 LOLLIPOP_MR1} and older platforms. On
         * newer platforms the subject defaults to {@code CN=fake} if not specified.
         */
        public Builder setSubject(X500Principal subject) {
            if (subject == null) {
                throw new NullPointerException("subject == null");
            }
            mSubjectDN = subject;
            return this;
        }

        /**
         * Sets the serial number used for the self-signed certificate of the
         * generated key pair.
         *
         * <p>The serial number must be specified on API Level
         * {@link android.os.Build.VERSION_CODES#LOLLIPOP_MR1 LOLLIPOP_MR1} and older platforms. On
         * newer platforms the serial number defaults to {@code 1} if not specified.
         */
        public Builder setSerialNumber(BigInteger serialNumber) {
            if (serialNumber == null) {
                throw new NullPointerException("serialNumber == null");
            }
            mSerialNumber = serialNumber;
            return this;
        }

        /**
         * Sets the start of the validity period for the self-signed certificate
         * of the generated key pair.
         *
         * <p>The date must be specified on API Level
         * {@link android.os.Build.VERSION_CODES#LOLLIPOP_MR1 LOLLIPOP_MR1} and older platforms. On
         * newer platforms the date defaults to {@code Jan 1 1970} if not specified.
         */
        public Builder setStartDate(Date startDate) {
            if (startDate == null) {
                throw new NullPointerException("startDate == null");
            }
            mStartDate = startDate;
            return this;
        }

        /**
         * Sets the end of the validity period for the self-signed certificate
         * of the generated key pair.
         *
         * <p>The date must be specified on API Level
         * {@link android.os.Build.VERSION_CODES#LOLLIPOP_MR1 LOLLIPOP_MR1} and older platforms. On
         * newer platforms the date defaults to {@code Jan 1 2048} if not specified.
         */
        public Builder setEndDate(Date endDate) {
            if (endDate == null) {
                throw new NullPointerException("endDate == null");
            }
            mEndDate = endDate;
            return this;
        }

        /**
         * Indicates that this key must be encrypted at rest on storage. Note
         * that enabling this will require that the user enable a strong lock
         * screen (e.g., PIN, password) before creating or using the generated
         * key is successful.
         */
        public Builder setEncryptionRequired() {
            mFlags |= KeyStore.FLAG_ENCRYPTED;
            return this;
        }

        /**
         * Sets the time instant before which the key is not yet valid.
         *
         * <p>By default, the key is valid at any instant.
         *
         * @see #setKeyValidityEnd(Date)
         *
         * @hide
         */
        public Builder setKeyValidityStart(Date startDate) {
            mKeyValidityStart = startDate;
            return this;
        }

        /**
         * Sets the time instant after which the key is no longer valid.
         *
         * <p>By default, the key is valid at any instant.
         *
         * @see #setKeyValidityStart(Date)
         * @see #setKeyValidityForConsumptionEnd(Date)
         * @see #setKeyValidityForOriginationEnd(Date)
         *
         * @hide
         */
        public Builder setKeyValidityEnd(Date endDate) {
            setKeyValidityForOriginationEnd(endDate);
            setKeyValidityForConsumptionEnd(endDate);
            return this;
        }

        /**
         * Sets the time instant after which the key is no longer valid for encryption and signing.
         *
         * <p>By default, the key is valid at any instant.
         *
         * @see #setKeyValidityForConsumptionEnd(Date)
         *
         * @hide
         */
        public Builder setKeyValidityForOriginationEnd(Date endDate) {
            mKeyValidityForOriginationEnd = endDate;
            return this;
        }

        /**
         * Sets the time instant after which the key is no longer valid for decryption and
         * verification.
         *
         * <p>By default, the key is valid at any instant.
         *
         * @see #setKeyValidityForOriginationEnd(Date)
         *
         * @hide
         */
        public Builder setKeyValidityForConsumptionEnd(Date endDate) {
            mKeyValidityForConsumptionEnd = endDate;
            return this;
        }

        /**
         * Sets the set of purposes for which the key can be used.
         *
         * <p>This must be specified for all keys. There is no default.
         *
         * @hide
         */
        public Builder setPurposes(@KeyStoreKeyProperties.PurposeEnum int purposes) {
            mPurposes = purposes;
            return this;
        }

        /**
         * Sets the set of digests with which the key can be used when signing/verifying. Attempts
         * to use the key with any other digest will be rejected.
         *
         * <p>This must be specified for keys which are used for signing/verification.
         *
         * @hide
         */
        public Builder setDigests(String... digests) {
            mDigests = ArrayUtils.cloneIfNotEmpty(digests);
            return this;
        }

        /**
         * Sets the set of padding schemes with which the key can be used when
         * encrypting/decrypting. Attempts to use the key with any other padding scheme will be
         * rejected.
         *
         * <p>This must be specified for keys which are used for encryption/decryption.
         *
         * @hide
         */
        public Builder setEncryptionPaddings(String... paddings) {
            mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(paddings);
            return this;
        }

        /**
         * Sets the set of padding schemes with which the key can be used when
         * signing/verifying. Attempts to use the key with any other padding scheme will be
         * rejected.
         *
         * <p>This must be specified for RSA keys which are used for signing/verification.
         *
         * @hide
         */
        public Builder setSignaturePaddings(String... paddings) {
            mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(paddings);
            return this;
        }

        /**
         * Sets the set of block modes with which the key can be used when encrypting/decrypting.
         * Attempts to use the key with any other block modes will be rejected.
         *
         * <p>This must be specified for encryption/decryption keys.
         *
         * @hide
         */
        public Builder setBlockModes(String... blockModes) {
            mBlockModes = ArrayUtils.cloneIfNotEmpty(blockModes);
            return this;
        }

        /**
         * Sets whether encryption using this key must be sufficiently randomized to produce
         * different ciphertexts for the same plaintext every time. The formal cryptographic
         * property being required is <em>indistinguishability under chosen-plaintext attack
         * ({@code IND-CPA})</em>. This property is important because it mitigates several classes
         * of weaknesses due to which ciphertext may leak information about plaintext. For example,
         * if a given plaintext always produces the same ciphertext, an attacker may see the
         * repeated ciphertexts and be able to deduce something about the plaintext.
         *
         * <p>By default, {@code IND-CPA} is required.
         *
         * <p>When {@code IND-CPA} is required, encryption/decryption transformations which do not
         * offer {@code IND-CPA}, such as RSA without padding, are prohibited.
         *
         * <p>Before disabling this requirement, consider the following approaches instead:
         * <ul>
         * <li>If you are using RSA encryption without padding, consider switching to padding
         * schemes which offer {@code IND-CPA}, such as PKCS#1 or OAEP.</li>
         * </ul>
         *
         * @hide
         */
        public Builder setRandomizedEncryptionRequired(boolean required) {
            mRandomizedEncryptionRequired = required;
            return this;
        }

        /**
         * Sets the user authenticators which protect access to this key. The key can only be used
         * iff the user has authenticated to at least one of these user authenticators.
         *
         * <p>By default, the key can be used without user authentication.
         *
         * <p>This restriction applies only to private key operations. Public key operations are not
         * restricted.
         *
         * @param userAuthenticators user authenticators or {@code 0} if this key can be accessed
         *        without user authentication.
         *
         * @see #setUserAuthenticationValidityDurationSeconds(int)
         *
         * @hide
         */
        public Builder setUserAuthenticators(
                @KeyStoreKeyProperties.UserAuthenticatorEnum int userAuthenticators) {
            mUserAuthenticators = userAuthenticators;
            return this;
        }

        /**
         * Sets the duration of time (seconds) for which this key can be used after the user
         * successfully authenticates to one of the associated user authenticators.
         *
         * <p>By default, the user needs to authenticate for every use of the key.
         *
         * <p>This restriction applies only to private key operations. Public key operations are not
         * restricted.
         *
         * @param seconds duration in seconds or {@code 0} if the user needs to authenticate for
         *        every use of the key.
         *
         * @see #setUserAuthenticators(int)
         *
         * @hide
         */
        public Builder setUserAuthenticationValidityDurationSeconds(int seconds) {
            mUserAuthenticationValidityDurationSeconds = seconds;
            return this;
        }

        /**
         * Builds the instance of the {@code KeyPairGeneratorSpec}.
         *
         * @throws IllegalArgumentException if a required field is missing
         * @return built instance of {@code KeyPairGeneratorSpec}
         */
        public KeyPairGeneratorSpec build() {
            return new KeyPairGeneratorSpec(mContext,
                    mKeystoreAlias,
                    mKeyType,
                    mKeySize,
                    mSpec,
                    mSubjectDN,
                    mSerialNumber,
                    mStartDate,
                    mEndDate,
                    mFlags,
                    mKeyValidityStart,
                    mKeyValidityForOriginationEnd,
                    mKeyValidityForConsumptionEnd,
                    mPurposes,
                    mDigests,
                    mEncryptionPaddings,
                    mSignaturePaddings,
                    mBlockModes,
                    mRandomizedEncryptionRequired,
                    mUserAuthenticators,
                    mUserAuthenticationValidityDurationSeconds);
        }
    }
}
