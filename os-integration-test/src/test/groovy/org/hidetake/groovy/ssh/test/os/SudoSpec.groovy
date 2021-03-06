package org.hidetake.groovy.ssh.test.os

import org.hidetake.groovy.ssh.Ssh
import org.hidetake.groovy.ssh.core.Service
import spock.lang.Specification
import spock.lang.Unroll

import static org.hidetake.groovy.ssh.test.os.Fixture.createRemotes
import static org.hidetake.groovy.ssh.test.os.Fixture.randomInt

/**
 * Check if {@link org.hidetake.groovy.ssh.session.execution.Sudo} works with Linux system.
 *
 * @author Hidetake Iwata
 */
class SudoSpec extends Specification {

    private static final user1 = "groovyssh${randomInt()}a"
    private static final user2 = "groovyssh${randomInt()}b"

    Service ssh

    def setup() {
        ssh = Ssh.newService()
        createRemotes(ssh)
        ssh.settings.extensions.add(UserManagementExtension)
    }

    @Unroll
    def 'should execute a privileged command on #sudoSpec'() {
        given:
        def autoGeneratedPassword = UUID.randomUUID().toString()
        ssh.run {
            session(ssh.remotes.Default) {
                recreateUser(user1)
                configurePassword(user1, autoGeneratedPassword)
                configureAuthorizedKeysAsCurrentUser(user1)
                configureSudo(user1, sudoSpec)
            }
        }

        and:
        ssh.remotes {
            PrivilegedUser {
                host = ssh.remotes.Default.host
                port = ssh.remotes.Default.port
                identity = ssh.remotes.Default.identity
                knownHosts = ssh.remotes.Default.knownHosts
                user = user1
            }
        }

        when:
        def whoami = ssh.run {
            session(ssh.remotes.PrivilegedUser) {
                executeSudo 'whoami', pty: true, sudoPassword: autoGeneratedPassword
            }
        }

        then:
        whoami == 'root'

        where:
        sudoSpec << ['ALL=(ALL) ALL', 'ALL=(ALL) NOPASSWD: ALL']
    }

    @Unroll
    def 'should execute a command as another user on #sudoSpec'() {
        given:
        def autoGeneratedPassword = UUID.randomUUID().toString()
        ssh.run {
            session(ssh.remotes.Default) {
                recreateUser(user1)
                configurePassword(user1, autoGeneratedPassword)
                configureAuthorizedKeysAsCurrentUser(user1)

                recreateUser(user2)
                configureAuthorizedKeysAsCurrentUser(user2)
            }
        }

        and:
        ssh.remotes {
            PrivilegedUser {
                host = ssh.remotes.Default.host
                port = ssh.remotes.Default.port
                identity = ssh.remotes.Default.identity
                knownHosts = ssh.remotes.Default.knownHosts
                user = user1
            }
        }

        when:
        def whoami = ssh.run {
            session(ssh.remotes.PrivilegedUser) {
                executeSudo "-u $user2 whoami", pty: true, sudoPassword: autoGeneratedPassword
            }
        }

        then:
        whoami == user2

        where:
        sudoSpec << ['ALL=(ALL) ALL', 'ALL=(ALL) NOPASSWD: ALL']
    }

}
