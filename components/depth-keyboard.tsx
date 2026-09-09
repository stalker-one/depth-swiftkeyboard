'use client'

import {
  ArrowRight,
  Check,
  Clipboard,
  Download,
  Globe2,
  Languages,
  MessageCircle,
  Palette,
  Search,
  ShieldCheck,
  Sparkles,
  Sticker,
  WandSparkles,
  Zap,
} from 'lucide-react'

const apkDownloadUrl = 'https://github.com/stalker-one/depth-swiftkeyboard/releases/download/Depth-SwiftKeyboard/depth-keyboard.apk'

const capabilities = [
  ['01', 'Smartbar', 'Keep emoji, clipboard, language switching, search and settings one tap away.'],
  ['02', 'Typing', 'Use local predictions, autocorrect, caps lock, double-space punctuation and accents.'],
  ['03', 'Layouts', 'Choose QWERTY, QWERTZ, AZERTY, DVORAK or COLEMAK with size and height controls.'],
  ['04', 'Languages', 'Switch local language layouts without breaking your flow.'],
  ['05', 'Emoji + Clips', 'Browse emoji categories, recent emoji and local clipboard history.'],
  ['06', 'Privacy', 'Incognito mode, local learning, clear-data controls and no bundled online provider.'],
]

const languages = ['English', 'اردو', 'العربية', 'Español', 'Deutsch', 'Français']

export default function DepthKeyboard() {
  return (
    <main className="depth-app feature-home modern-home">
      <div className="ambient ambient-one" />
      <div className="ambient ambient-two" />

      <header className="modern-nav">
        <a className="modern-brand" href="#top" aria-label="Depth Keyboard home">
          <span className="brand-mark"><Zap size={18} /></span>
          <span><b>DEPTH</b><small>KEYBOARD</small></span>
        </a>
        <nav className="modern-links" aria-label="Main navigation">
          <a href="#features">Features</a><a href="#ai">AI</a><a href="#privacy">Privacy</a><a href="#download">Download</a>
        </nav>
        <a className="nav-cta" href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={15} /> Get APK</a>
      </header>

      <section id="top" className="modern-hero">
        <div className="hero-copy modern-hero-copy">
          <div className="status-badge"><span /> Built for Android · 8+</div>
          <h1>Your keyboard.<br /><span>Your intelligence.</span></h1>
          <p>Depth is a modern, private-first keyboard built around fast local typing, rich input, flexible layouts and settings you can actually control.</p>
          <div className="hero-actions">
            <a className="download-button hero-button" href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={17} /> Download for Android <ArrowRight size={16} /></a>
            <a className="text-cta" href="#features">Explore features <ArrowRight size={15} /></a>
          </div>
          <div className="hero-proof"><span><ShieldCheck size={14} /> Local-first</span><span><Sparkles size={14} /> Customizable</span><span><Globe2 size={14} /> Multilingual</span></div>
        </div>

        <div className="hero-device" aria-label="Depth Keyboard interactive preview">
          <div className="device-glow" />
          <div className="device-card">
            <div className="device-camera" />
            <div className="device-screen">
              <div className="screen-status"><span>9:41</span><span>● ● ▰</span></div>
              <div className="chat-window">
                <div className="chat-label">MESSAGES <span>today</span></div>
                <div className="bubble bubble-left">Hey! Can you send the details?</div>
                <div className="bubble bubble-right">Absolutely — I’ll send them shortly.</div>
                <div className="compose-line">Write a reply<span className="cursor" /></div>
              </div>
              <div className="keyboard-preview">
                <div className="ai-row"><b>Absolutely</b><span>Sure</span><span>Of course</span><Sparkles size={14} /></div>
                <div className="key-grid">{'qwertyuiopasdfghjklzxcvbnm'.split('').map((key) => <i key={key}>{key}</i>)}</div>
                <div className="space-row"><i>☺</i><i>🌐</i><b>space</b><i>⌫</i><i className="enter">↵</i></div>
              </div>
            </div>
          </div>
          <div className="floating-stat stat-one"><Sparkles size={15} /><b>AI Compose</b><small>Ready when you are</small></div>
          <div className="floating-stat stat-two"><span className="live-dot" /><b>Local first</b><small>Your controls, your data</small></div>
        </div>
      </section>

      <section className="marquee-section" aria-label="Depth capabilities">
        <div className="marquee-track">{['SMARTBAR', 'LONG PRESS', 'CAPS LOCK', 'DOUBLE SPACE', 'EMOJI', 'CLIPBOARD', 'DVORAK', 'COLEMAK', 'THEMES', 'LAYOUTS', 'INCOGNITO', 'PRIVACY'].map((item, i) => <span key={`${item}-${i}`}>{item}<b>✦</b></span>)}</div>
      </section>

      <section id="features" className="modern-section features-modern">
        <div className="section-intro"><span className="section-number">01</span><div><p className="eyebrow">THE TOOLKIT</p><h2>Everything you need.<br /><em>Nothing in the way.</em></h2></div><p>One keyboard that keeps your everyday tools close, from the first letter to the final send.</p></div>
        <div className="capability-grid">{capabilities.map(([num, title, text]) => <article className="capability-card" key={num}><span className="cap-num">{num}</span><div className="cap-icon">{num === '01' ? <WandSparkles /> : num === '02' ? <Zap /> : num === '03' ? <MessageCircle /> : num === '04' ? <Languages /> : num === '05' ? <Sticker /> : <ShieldCheck />}</div><h3>{title}</h3><p>{text}</p><ArrowRight className="cap-arrow" size={18} /></article>)}</div>
      </section>

      <section id="ai" className="ai-modern modern-section">
        <div className="ai-copy"><span className="section-number">02</span><p className="eyebrow">CONTROL CENTER</p><h2>Make it yours.<br /><em>One setting at a time.</em></h2><p>Adjust keyboard size, height, width, themes, layouts, accents, emoji behavior, clipboard history, privacy and input feedback from a modern grouped settings screen.</p><div className="ai-list"><span><Check /> Size + height</span><span><Check /> Layout modes</span><span><Check /> Emoji categories</span><span><Check /> Privacy controls</span></div></div>
        <div className="ai-window">
          <div className="ai-window-top"><span><Sparkles size={15} /> DEPTH SETTINGS</span><small>Keyboard control center</small></div>
          <div className="ai-prompt"><small>ACTIVE PROFILE</small><p>Midnight · QWERTY · 110% size · Local learning</p></div>
          <div className="ai-actions"><button>Typing</button><button>Layouts</button><button>Privacy</button></div>
          <div className="ai-answer"><span><Sparkles size={14} /> READY</span><p>Long-press accents, double-space punctuation, emoji recents and clipboard history are available from the keyboard.</p><button><Clipboard size={14} /> Explore</button></div>
        </div>
      </section>

      <section className="tools-modern modern-section">
        <div className="tools-visual"><div className="tool-orbit orbit-a" /><div className="tool-orbit orbit-b" /><div className="tool-center"><Sparkles size={30} /></div><div className="tool-node node-a"><Search /></div><div className="tool-node node-b"><Languages /></div><div className="tool-node node-c"><Clipboard /></div><div className="tool-node node-d"><Sticker /></div></div>
        <div className="tools-copy"><span className="section-number">03</span><p className="eyebrow">ONE TAP AWAY</p><h2>Your everyday tools, <em>from the keyboard.</em></h2><p>Open local clipboard history, emoji categories, language switching, settings and browser search without leaving the text field.</p><div className="tool-pills"><span><Search /> Search</span><span><Languages /> Languages</span><span><Clipboard /> Clipboard</span><span><Sticker /> Emoji</span></div></div>
      </section>

      <section id="privacy" className="privacy-modern modern-section">
        <div className="privacy-panel"><div className="privacy-icon"><ShieldCheck size={25} /></div><div><p className="eyebrow">04 · PRIVATE BY DESIGN</p><h2>Your typing belongs to you.</h2><p>Use Incognito, control personalization, manage learned language data and decide what ever leaves your device. Cloud sync can stay optional.</p></div><div className="privacy-points"><span><Check /> Incognito mode</span><span><Check /> Learning controls</span><span><Check /> Backup & sync controls</span><span><Check /> Data management</span></div></div>
      </section>

      <section className="languages-modern modern-section"><div className="section-intro"><span className="section-number">05</span><div><p className="eyebrow">YOUR LANGUAGES</p><h2>Speak your way.</h2></div><p>Designed for multilingual conversations with fast switching, language-specific predictions and flexible layouts.</p></div><div className="language-cloud">{languages.map((language, i) => <span className={i === 0 ? 'language-active' : ''} key={language}>{language}</span>)}<span>＋ Add language</span></div></section>

      <section id="download" className="download-modern modern-section"><div><p className="eyebrow">06 · READY WHEN YOU ARE</p><h2>Make your keyboard<br /><em>feel like yours.</em></h2><p>Download the latest Android build and start shaping your typing experience.</p></div><div className="download-box"><div className="download-mark"><Zap size={24} /></div><div><b>Depth Keyboard</b><small>Android 8+ · Latest APK</small></div><a href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={17} /> Download</a></div></section>

      <footer className="modern-footer"><div className="modern-brand"><span className="brand-mark"><Zap size={16} /></span><span><b>DEPTH</b><small>KEYBOARD</small></span></div><p>Built for your flow. Designed for your control.</p><a href="https://github.com/stalker-one/depth-swiftkeyboard">GitHub <ArrowRight size={14} /></a></footer>
    </main>
  )
}
