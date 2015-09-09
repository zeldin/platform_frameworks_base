/*
 * Copyright (C) 2013 The Android Open Source Project
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

import java.security.Key;
import java.security.KeyStore.ProtectionParameter;
import java.util.Date;

import javax.crypto.Cipher;

/**
 * Parameters specifying how to secure and restrict the use of a key being
 * imported into the
 * <a href="{@docRoot}training/articles/keystore.html">Android KeyStore
 * facility</a>. The Android KeyStore facility is accessed through a
 * {@link java.security.KeyStore} API using the {@code AndroidKeyStore}
 * provider. The {@code context} passed in may be used to pop up some UI to ask
 * the user to unlock or initialize the Android KeyStore facility.
 * <p>
 * Any entries placed in the {@code KeyStore} may be retrieved later. Note that
 * there is only one logical instance of the {@code KeyStore} per application
 * UID so apps using the {@code sharedUid} facility will also share a
 * {@code KeyStore}.
 */
public final class KeyStoreParameter implements ProtectionParameter {
    private int mFlags;
    private final Date mKeyValidityStart;
    private final Date mKeyValidityForOriginationEnd;
    private final Date mKeyValidityForConsumptionEnd;
    private final @KeyStoreKeyProperties.PurposeEnum int mPurposes;
    private final String[] mEncryptionPaddings;
    private final String[] mSignaturePaddings;
    private final String[] mDigests;
    private final String[] mBlockModes;
    private final boolean mRandomizedEncryptionRequired;
    private final @KeyStoreKeyProperties.UserAuthenticatorEnum int mUserAuthenticators;
    private final int mUserAuthenticationValidityDurationSeconds;

    private KeyStoreParameter(int flags,
            Date keyValidityStart,
            Date keyValidityForOriginationEnd,
            Date keyValidityForConsumptionEnd,
            @KeyStoreKeyProperties.PurposeEnum int purposes,
            String[] encryptionPaddings,
            String[] signaturePaddings,
            String[] digests,
            String[] blockModes,
            boolean randomizedEncryptionRequired,
            @KeyStoreKeyProperties.UserAuthenticatorEnum int userAuthenticators,
            int userAuthenticationValidityDurationSeconds) {
        if ((userAuthenticationValidityDurationSeconds < 0)
                && (userAuthenticationValidityDurationSeconds != -1)) {
            throw new IllegalArgumentException(
                    "userAuthenticationValidityDurationSeconds must not be negative");
        }

        mFlags = flags;
        mKeyValidityStart = keyValidityStart;
        mKeyValidityForOriginationEnd = keyValidityForOriginationEnd;
        mKeyValidityForConsumptionEnd = keyValidityForConsumptionEnd;
        mPurposes = purposes;
        mEncryptionPaddings =
                ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(encryptionPaddings));
        mSignaturePaddings =
                ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(signaturePaddings));
        mDigests = ArrayUtils.cloneIfNotEmpty(digests);
        mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(blockModes));
        mRandomizedEncryptionRequired = randomizedEncryptionRequired;
        mUserAuthenticators = userAuthenticators;
        mUserAuthenticationValidityDurationSeconds = userAuthenticationValidityDurationSeconds;
    }

    /**
     * @hide
     */
    public int getFlags() {
        return mFlags;
    }

    /**
     * Returns {@code true} if this parameter requires entries to be encrypted
     * on the disk.
     */
    public boolean isEncryptionRequired() {
        return (mFlags & KeyStore.FLAG_ENCRYPTED) != 0;
    }

    /**
     * Gets the time instant before which the key is not yet valid.
     *
     * @return instant or {@code null} if not restricted.
     * @hide
     */
    public Date getKeyValidityStart() {
        return mKeyValidityStart;
    }

    /**
     * Gets the time instant after which the key is no long valid for decryption and verification.
     *
     * @return instant or {@code null} if not restricted.
     *
     * @hide
     */
    public Date getKeyValidityForConsumptionEnd() {
        return mKeyValidityForConsumptionEnd;
    }

    /**
     * Gets the time instant after which the key is no long valid for encryption and signing.
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
     * Gets the set of padding schemes with which the key can be used when encrypting/decrypting.
     *
     * @hide
     */
    public String[] getEncryptionPaddings() {
        return ArrayUtils.cloneIfNotEmpty(mEncryptionPaddings);
    }

    /**
     * Gets the set of padding schemes with which the key can be used when signing or verifying
     * signatures.
     *
     * @hide
     */
    public String[] getSignaturePaddings() {
        return ArrayUtils.cloneIfNotEmpty(mSignaturePaddings);
    }

    /**
     * Gets the set of digest algorithms with which the key can be used.
     *
     * @throws IllegalStateException if this set has not been specified.
     *
     * @see #isDigestsSpecified()
     *
     * @hide
     */
    public String[] getDigests() {
        if (mDigests == null) {
            throw new IllegalStateException("Digests not specified");
        }
        return ArrayUtils.cloneIfNotEmpty(mDigests);
    }

    /**
     * Returns {@code true} if the set of digest algorithms with which the key can be used has been
     * specified.
     *
     * @see #getDigests()
     *
     * @hide
     */
    public boolean isDigestsSpecified() {
        return mDigests != null;
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
     * weaknesses due to which ciphertext may leak information about plaintext. For example, if a
     * given plaintext always produces the same ciphertext, an attacker may see the repeated
     * ciphertexts and be able to deduce something about the plaintext.
     *
     * @hide
     */
    public boolean isRandomizedEncryptionRequired() {
        return mRandomizedEncryptionRequired;
    }

    /**
     * Gets the set of user authenticators which protect access to this key. The key can only be
     * used iff the user has authenticated to at least one of these user authenticators.
     *
     * @return user authenticators or {@code 0} if the key can be used without user authentication.
     *
     * @hide
     */
    public @KeyStoreKeyProperties.UserAuthenticatorEnum int getUserAuthenticators() {
        return mUserAuthenticators;
    }

    /**
     * Gets the duration of time (seconds) for which this key can be used after the user
     * successfully authenticates to one of the associated user authenticators.
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
     * Builder class for {@link KeyStoreParameter} objects.
     * <p>
     * This will build protection parameters for use with the
     * <a href="{@docRoot}training/articles/keystore.html">Android KeyStore
     * facility</a>.
     * <p>
     * This can be used to require that KeyStore entries be stored encrypted.
     * <p>
     * Example:
     *
     * <pre class="prettyprint">
     * KeyStoreParameter params = new KeyStoreParameter.Builder(mContext)
     *         .setEncryptionRequired()
     *         .build();
     * </pre>
     */
    public final static class Builder {
        private int mFlags;
        private Date mKeyValidityStart;
        private Date mKeyValidityForOriginationEnd;
        private Date mKeyValidityForConsumptionEnd;
        private @KeyStoreKeyProperties.PurposeEnum int mPurposes;
        private String[] mEncryptionPaddings;
        private String[] mSignaturePaddings;
        private String[] mDigests;
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

            // Context is currently not used, but will be in the future.
        }

        /**
         * Indicates that this key must be encrypted at rest on storage. Note
         * that enabling this will require that the user enable a strong lock
         * screen (e.g., PIN, password) before creating or using the generated
         * key is successful.
         */
        public Builder setEncryptionRequired(boolean required) {
            if (required) {
                mFlags |= KeyStore.FLAG_ENCRYPTED;
            } else {
                mFlags &= ~KeyStore.FLAG_ENCRYPTED;
            }
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
         * Sets the set of digests with which the key can be used when signing/verifying or
         * generating MACs. Attempts to use the key with any other digest will be rejected.
         *
         * <p>For HMAC keys, the default is the digest specified in {@link Key#getAlgorithm()}. For
         * asymmetric signing keys this constraint must be specified.
         *
         * @hide
         */
        public Builder setDigests(String... digests) {
            mDigests = ArrayUtils.cloneIfNotEmpty(digests);
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
         * <p>When {@code IND-CPA} is required:
         * <ul>
         * <li>transformation which do not offer {@code IND-CPA}, such as symmetric ciphers using
         * {@code ECB} mode or RSA encryption without padding, are prohibited;</li>
         * <li>in transformations which use an IV, such as symmetric ciphers in {@code CBC},
         * {@code CTR}, and {@code GCM} block modes, caller-provided IVs are rejected when
         * encrypting, to ensure that only random IVs are used.</li>
         *
         * <p>Before disabling this requirement, consider the following approaches instead:
         * <ul>
         * <li>If you are generating a random IV for encryption and then initializing a {@code}
         * Cipher using the IV, the solution is to let the {@code Cipher} generate a random IV
         * instead. This will occur if the {@code Cipher} is initialized for encryption without an
         * IV. The IV can then be queried via {@link Cipher#getIV()}.</li>
         * <li>If you are generating a non-random IV (e.g., an IV derived from something not fully
         * random, such as the name of the file being encrypted, or transaction ID, or password,
         * or a device identifier), consider changing your design to use a random IV which will then
         * be provided in addition to the ciphertext to the entities which need to decrypt the
         * ciphertext.</li>
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
         * Builds the instance of the {@code KeyStoreParameter}.
         *
         * @throws IllegalArgumentException if a required field is missing
         * @return built instance of {@code KeyStoreParameter}
         */
        public KeyStoreParameter build() {
            return new KeyStoreParameter(mFlags,
                    mKeyValidityStart,
                    mKeyValidityForOriginationEnd,
                    mKeyValidityForConsumptionEnd,
                    mPurposes,
                    mEncryptionPaddings,
                    mSignaturePaddings,
                    mDigests,
                    mBlockModes,
                    mRandomizedEncryptionRequired,
                    mUserAuthenticators,
                    mUserAuthenticationValidityDurationSeconds);
        }
    }
}
