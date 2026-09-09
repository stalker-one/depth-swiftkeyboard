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
  ['01', 'AI Assist', 'Rewrite, polish, brainstorm and compose without leaving the app.'],
  ['02', 'Flow Typing', 'Swipe, predict and complete words at the speed of your thoughts.'],
  ['03', 'Voice', 'Dictate naturally with fast voice-to-text input.'],
  ['04', 'Languages', 'Switch languages and layouts without breaking your flow.'],
  ['05', 'Rich Input', 'Emoji, GIFs, stickers, search and clipboard in one toolbar.'],
  ['06', 'Privacy', 'Local-first controls with Incognito and learning preferences.'],
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
          <p>Depth is a modern, private-first keyboard built around how people actually communicate — fast typing, AI assistance, rich input and total control.</p>
          <div className="hero-actions">
            <a className="download-button hero-button" href={apkDownloadUrl} download="depth-keyboard.apk"><Download size={17} /> Download for Android <ArrowRight size={16} /></a>
            <a className="text-cta" href="#features">Explore features <ArrowRight size={15} /></a>
          </div>
          <div className="hero-proof"><span><ShieldCheck size={14} /> Privacy controls</span><span><Sparkles size={14} /> AI-ready</span><span><Globe2 size={14} /> Multilingual</span></div>
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
        <div className="marquee-track">{['AI ASSIST', 'FLOW TYPING', 'VOICE', '700+ LANGUAGE GOAL', 'EMOJI', 'GIFS', 'STICKERS', 'CLIPBOARD', 'TRANSLATOR', 'SEARCH', 'THEMES', 'PRIVACY'].map((item, i) => <span key={`${item}-${i}`}>{item}<b>✦</b></span>)}</div>
      </section>

      <section id="features" className="modern-section features-modern">
        <div className="section-intro"><span className="section-number">01</span><div><p className="eyebrow">THE TOOLKIT</p><h2>Everything you need.<br /><em>Nothing in the way.</em></h2></div><p>One keyboard that keeps your everyday tools close, from the first letter to the final send.</p></div>
        <div className="capability-grid">{capabilities.map(([num, title, text]) => <article className="capability-card" key={num}><span className="cap-num">{num}</span><div className="cap-icon">{num === '01' ? <WandSparkles /> : num === '02' ? <Zap /> : num === '03' ? <MessageCircle /> : num === '04' ? <Languages /> : num === '05' ? <Sticker /> : <ShieldCheck />}</div><h3>{title}</h3><p>{text}</p><ArrowRight className="cap-arrow" size={18} /></article>)}</div>
      </section>

      <section id="ai" className="ai-modern modern-section">
        <div className="ai-copy"><span className="section-number">02</span><p className="eyebrow">DEPTH AI</p><h2>Think it.<br /><em>Say it better.</em></h2><p>Turn rough thoughts into clear messages. Rewrite tone, shorten a paragraph, make it professional, or start from an idea — right above the keyboard.</p><div className="ai-list"><span><Check /> Professional</span><span><Check /> Casual</span><span><Check /> Polite</span><span><Check /> Social post</span></div></div>
        <div className="ai-window">
          <div className="ai-window-top"><span><Sparkles size={15} /> DEPTH AI</span><small>Compose assistant</small></div>
          <div className="ai-prompt"><small>YOUR DRAFT</small><p>can you send me the project details when you get time</p></div>
          <div className="ai-actions"><button>Professional</button><button>Friendly</button><button>Shorter</button></div>
          <div className="ai-answer"><span><Sparkles size={14} /> READY</span><p>Could you please send me the project details when you have a moment?</p><button><Clipboard size={14} /> Copy</button></div>
        </div>
      </section>

      <section className="tools-modern modern-section">
        <div className="tools-visual"><div className="tool-orbit orbit-a" /><div className="tool-orbit orbit-b" /><div className="tool-center"><Sparkles size={30} /></div><div className="tool-node node-a"><Search /></div><div className="tool-node node-b"><Languages /></div><div className="tool-node node-c"><Clipboard /></div><div className="tool-node node-d"><Sticker /></div></div>
        <div className="tools-copy"><span className="section-number">03</span><p className="eyebrow">ONE TAP AWAY</p><h2>Your whole internet life, <em>from the keyboard.</em></h2><p>Search the web, translate, find a GIF, grab a saved phrase or drop an emoji without jumping between apps.</p><div className="tool-pills"><span><Search /> Search</span><span><Languages /> Translate</span><span><Clipboard /> Clipboard</span><span><Sticker /> GIFs & stickers</span></div></div>
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
