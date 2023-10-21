const {createServer} = require("https")
const {parse} = require("url")
const next = require("next")
const fs = require("fs")

const app = next({dev: false})
const handle = app.getRequestHandler()

const privateKeyPath = "./ssl/privkey1.pem"
const certPath = "./ssl/cert1.pem"
const chainPath = "./ssl/chain1.pem"
const port = process.argv[2] || 3000

app.prepare().then(() => {
    createServer({
        key: fs.readFileSync(privateKeyPath),
        cert: fs.readFileSync(certPath),
        ca: fs.readFileSync(chainPath)
    }, async (req, res) => {
        const parsedUrl = parse(req.url, true)
        await handle(req, res, parsedUrl)
    }).listen(port, (err) => {
        if (err) throw err
        console.log("ready - started server on url: https://localhost:" + port)
    })
})
