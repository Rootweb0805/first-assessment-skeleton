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
// RIP newContents
// let newContents

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
      let message = Message.fromJSON(buffer)
      let value = message.toString()
      if (message.command === 'echo') {
        cli.log(cli.chalk['cyan'](value))
      } else if (message.command === 'users') {
        cli.log(cli.chalk['red'](value))
      } else if (message.command.substring(0, 1) === '@') {
        cli.log(cli.chalk['magenta'](value))
      } else if (message.command === 'broadcast') {
        cli.log(cli.chalk['blue'](value))
      } else {
        this.log(value)
      }
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    let [ command, ...rest ] = words(input, /[^, ]+/g)
    let contents = rest.join(' ')

    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'users') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command.substring(0, 1) === '@') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (previousCommand === 'echo') {
      contents = command + ' ' + contents
      command = 'echo'
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (previousCommand === 'users') {
      contents = command + ' ' + contents
      command = 'users'
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (previousCommand.substring(0, 1) === '@') {
      contents = command + ' ' + contents
      command = previousCommand
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (previousCommand === 'broadcast') {
      contents = command + ' ' + contents
      command = 'broadcast'
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else {
      this.log(`Command is required`)
    }

    // WHY DOES CAUSE NULL POINTER?!?!?!?!
    // } else if (previousCommand !== undefined) {
    //   newContents = command + ' ' + contents
    //   cli.log(previousCommand + ' ' + newContents)
    //   server.write(new Message({ username, previousCommand, newContents }).toJSON() + '\n')

    callback()
  })
