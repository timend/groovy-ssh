package org.hidetake.groovy.ssh.operation

import groovy.transform.EqualsAndHashCode
import org.hidetake.groovy.ssh.core.settings.LoggingMethod
import org.hidetake.groovy.ssh.core.settings.PlusProperties
import org.hidetake.groovy.ssh.core.settings.ToStringProperties

/**
 * Settings for {@link Command}.
 *
 * @author Hidetake Iwata
 */
@EqualsAndHashCode
class CommandSettings implements PlusProperties<CommandSettings>, ToStringProperties {
    /**
     * Ignores the exit status of the command or shell.
     */
    Boolean ignoreError

    /**
     * PTY allocation flag.
     * If <code>true</code>, PTY will be allocated on command execution.
     */
    Boolean pty

    /**
     * A logging method of the remote command or shell.
     */
    LoggingMethod logging

    /**
     * An output stream to forward the standard output.
     */
    OutputStream outputStream

    /**
     * An output stream to forward the standard error.
     */
    OutputStream errorStream

    /**
     * Encoding of input and output stream.
     */
    String encoding

    /**
     * Stream interaction.
     * @see org.hidetake.groovy.ssh.interaction.InteractionHandler
     */
    Closure interaction

    static final DEFAULT = new CommandSettings(
            ignoreError: false,
            pty: false,
            logging: LoggingMethod.slf4j,
            encoding: 'UTF-8',
    )
}