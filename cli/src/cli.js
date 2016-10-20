import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server
let address
let port
let previousCommand

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <address> <port> <username>')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    address = args.address
    port = args.port
    username = args.username
    server = connect({ host: address, port: port }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })

    server.on('data', (buffer) => {
      this.log(Message.fromJSON(buffer).toString())
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    const [ command, ...rest ] = words(input, /[^, ]+/g)
    const contents = rest.join(' ')

    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      previousCommand === command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'users') {
      previousCommand === command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command.substring(0, 1) === '@') {
      previousCommand === command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      previousCommand === command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === null) {
      command === previousCommand
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else {
      this.log(`Command <${command}> was not recognized`)
    }

    callback()
  })
